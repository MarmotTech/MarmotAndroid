package me.jinheng.cityullm.models;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import me.jinheng.cityullm.MessageAdapter;
import me.jinheng.cityullm.newui.CustomChat;

public class LLama {

    public static MessageAdapter messageAdapter;

    public static Long id = 0L;

    public static boolean answering = false;

    public static AnswerState answerState = AnswerState.NO_MESSAGE_NEED_REPLY;

    public static NativeMessageReceiver msg = new NativeMessageReceiver();

    public static String input;

    public static Thread curThread;

    static {
        System.loadLibrary("llama-jni");
    }

    public static void walkFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                Log.d("debug", "Directory: " + file.getAbsolutePath());
                walkFolder(file.getAbsolutePath());
            } else if (file.isFile()) {
                Log.d("debug", "File: " + file.getAbsolutePath() + ", size " + file.length());
            }
        }
    }

    public static void initFolder(File externalDir) {
        File llamaFolder = new File(externalDir, "llama");
        Config.basePath = llamaFolder.getAbsolutePath() + "/";

        File cppFolder = new File(llamaFolder, "main");
        Config.cppPath = cppFolder.getAbsolutePath() + "/";
        if (!cppFolder.exists()) {
            cppFolder.mkdirs();
        }

        File modelFolder = new File(llamaFolder, "models");
        Config.modelPath = modelFolder.getAbsolutePath() + "/";
        if (!modelFolder.exists()) {
            modelFolder.mkdirs();
        }

        File historyFolder = new File(llamaFolder, "history");
        Config.historyPath = historyFolder.getAbsolutePath() + "/";
        if (!historyFolder.exists()) {
            historyFolder.mkdirs();
        }

        File dataFolder = new File(llamaFolder, "data");
        Config.dataPath = dataFolder.getAbsolutePath() + "/";
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public static boolean hasInitialModel() {
        File modelFolder = new File(Config.modelPath);
        if (modelFolder.exists()) {
            File[] files = modelFolder.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(".gguf")) {
                    Log.d("debug", "Find initial model " + f.getAbsolutePath());
                    return true;
                }
            }
        } else {
            Log.d("debug", modelFolder.getAbsolutePath() + " does not exist");
        }
        return false;
    }

    public static native void inputString(String s);

    public static native void startChat(NativeMessageReceiver msg, String localModelPath, String systemPrompt, int threadNum);

    private static native void startChatWPrefetch(NativeMessageReceiver msg, String localModelPath, String systemPrompt, int threadNum, float prefetchSizeInGB, float lSize);

    public static native void stop();

    public static native void kill();

    public static void init(String modelName, boolean enablePrefetch, CustomChat chat) throws IOException {
        ModelInfo mInfo = ModelOperation.modelName2modelInfo.get(modelName);
        float totalMemory = Utils.getTotalMemory() / CONSTANT.GB;
        float canUseMemory = Math.min(totalMemory, Config.maxMemorySize);
        float modelSize = (float) mInfo.getModelSize() / CONSTANT.GB;

        float prefetchSizeInGB = 0f;
        float kvCacheSizeInGB = 0f;
        float lSize = 0f;

        if (canUseMemory <= modelSize) {
            prefetchSizeInGB = (float) mInfo.getPrefetchSize() / CONSTANT.GB;
            kvCacheSizeInGB = (float) mInfo.getKvSize() / CONSTANT.GB;
        }

        System.out.println("INIT: " + modelName + "\npath: " + mInfo.getModelLocalPath() + "\nprefetch: " + enablePrefetch);
        if (enablePrefetch) {
            startChatWPrefetch(msg, mInfo.getModelLocalPath(), mInfo.getSystemPrompt(), Config.threadNum, prefetchSizeInGB, lSize);
        } else {
            startChat(msg, mInfo.getModelLocalPath(), mInfo.getSystemPrompt(), Config.threadNum);
        }

        curThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                msg.reset();
                String s = msg.waitForString();

                if (answerState == AnswerState.NO_MESSAGE_NEED_REPLY) {
                    //
                } else if (answerState == AnswerState.MESSAGE_NEED_REPLY) {
                    answerState = AnswerState.ANSWERING;
                    chat.botContinue(s);
                } else {
                    if (msg.isStart()) {
                        chat.updateInfo(s);
                        answerState = AnswerState.NO_MESSAGE_NEED_REPLY;
                    } else {
                        chat.botContinue(s);
                    }
                }
            }
        });
        curThread.start();
    }

    public static void run(String input_) throws RuntimeException {
        input = input_;
        inputString(input);
        answerState = AnswerState.MESSAGE_NEED_REPLY;
    }

    public static void destroy() {
        kill();
        curThread.interrupt();
    }

    public static void clear() {
        messageAdapter.clear();
    }
}
