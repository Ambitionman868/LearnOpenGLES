package com.opengles.util;

import android.opengl.GLES20;
import android.util.Log;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Jianpan on 2017/10/27.
 */

public class ShaderHelper {

    private static final String TAG = "test";

    //编译着色器
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        //创建着色器对象
        final int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w("test", "Could not create new shader");
            }
            return 0;
        }
        //上传和编译着色器代码
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);

        //取出编译状态
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        //取出着色器信息日志
        if (LoggerConfig.ON) {
            Log.v("test", "Result of compileing source:" + "\n" + shaderCode + "\n" + GLES20.glGetShaderInfoLog(shaderObjectId));
        }

        //验证编译状态并返回着色器对象ID
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId);
            if (LoggerConfig.ON) {
                Log.w("test", "compleShader: Compilation of shader failed");
            }
            return 0;
        }


        return shaderObjectId;
    }

    //把着色器一起链接进OpenGL的程序
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        //新建程序对象
        final int programObjectId = GLES20.glCreateProgram();
        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w("test", "Could not create new program");
            }
            return 0;
        }

        //附上着色器(把顶点着色器和片段着色器都附加到程序对象上)
        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);

        //链接程序
        GLES20.glLinkProgram(programObjectId);
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (LoggerConfig.ON) {
            Log.i("test", "linkProgram: result of linking program" + GLES20.glGetProgramInfoLog(programObjectId));
        }

        //验证链接状态并返回程序对象Id
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programObjectId);
            if (LoggerConfig.ON) {
                Log.i(TAG, "linkProgram: linkging of program failed");
            }
            return 0;
        }
        return programObjectId;

    }

    //验证OpenGL程序的对象
    public static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        Log.i(TAG, "validateProgram: " + validateStatus[0] + "\n" + GLES20.glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;

    }


}
