package com.example.cek_sampah.ml;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class SampahClassifier {

    private Interpreter interpreter;
    private final int INPUT_SIZE = 224;
    private final static String TAG = "ML_DEBUG";

    public SampahClassifier(Context context) {
        try {
            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(4);

            MappedByteBuffer modelBuffer = loadModelFile(context, "model_fixed.tflite");
            interpreter = new Interpreter(modelBuffer, options);

            Log.d("ML_DEBUG", "Model Load Berhasil");

        } catch (Exception e) {
            Log.e("ML_DEBUG", "Load Error: " + e.getMessage());
        }
    }

    public float[] classifyImage(Bitmap bitmap) {
        if (interpreter == null) return new float[]{0, 0, 0};

        Bitmap argbBitmap = null;
        try {
            argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(argbBitmap);

            // sesuai training: 0-255 → 0-1
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(0.0f, 1.0f))
                    .build();

            tensorImage = imageProcessor.process(tensorImage);
            
            float[][] output = new float[1][3];

            interpreter.run(tensorImage.getBuffer(), output);

            Log.d(TAG, "Raw Prediction: " + Arrays.toString(output[0]));

            return output[0];

        } catch (Exception e) {
            Log.e(TAG, "Classification Error: " + e.getMessage());
            return new float[]{0, 0, 0};
        } finally {
            if (argbBitmap != null && ! argbBitmap.isRecycled()){
                argbBitmap.recycle();
            }
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();

        return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.getStartOffset(),
                fileDescriptor.getDeclaredLength()
        );
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
}