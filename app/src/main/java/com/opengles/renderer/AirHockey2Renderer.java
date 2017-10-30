package com.opengles.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.opengles.R;
import com.opengles.util.LoggerConfig;
import com.opengles.util.ShaderHelper;
import com.opengles.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Jianpan on 2017/10/27.
 */

public class AirHockey2Renderer implements GLSurfaceView.Renderer {

    private static final String U_MATRIX = "u_Matrix";
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    //
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    //跨距
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final FloatBuffer vertexData;
    private final Context mContext;

    private int program;
    private int aPositionLocation;
    private int aColorLocation;
    //顶点数组存储矩阵
    private final float[] projectionMatrix = new float[16];
    //存储矩阵的uniform位置
    private int uMatrixLocation;

    //在OpenGL里只能 绘制点，直线，以及三角形


    public AirHockey2Renderer(Context context) {
        mContext = context;
        //定义三角形是以逆时针顺序排列顶点，称为卷曲顺序
        /*float[] tableVerticesWithTriangles = {
                //Triangle 1
                0f, 0f,
                9f, 14f,
                0f, 14f,

                //Triangle 2
                0f, 0f,
                9f, 0f,
                9f, 14f,

                //Line 1
                0f, 7f,
                9f, 7f,

                // Mallets  木槌
                4.5f, 2f,
                4.5f, 12f
        };*/

        float[] tableVerticesWithTriangles = {

                //order of x,y,r,g,b

                //Triangle Fan

                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                //Line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // Mallets  木槌
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f
        };


        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        //设置清空屏幕用的颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //读入着色器(顶点着色器，片段着色器)
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        GLES20.glUseProgram(program);
        //获取uniform的位置并存入uColorLocation
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        //获取属性位置
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

        //关联属性与顶点数据的数组
        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);


        //当OpenGL读取颜色属性，从它的第一个颜色属性开始
        vertexData.position(POSITION_COMPONENT_COUNT);
        //把颜色数据和着色器中的a_Color关联起来
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        //使能顶点数组
        GLES20.glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        //设置视图尺寸
        GLES20.glViewport(0, 0, width, height);

        //创建正交投影矩阵
        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
        Matrix matrix = new Matrix();
        if (width > height) {
            matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, -1f);
        }
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);


    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //擦除屏幕上的所有颜色并用之前glClearColor()调用定义的颜色填充整个屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //指定颜色值  4个分量RGBA
        //参数： 图形形状，顶点开头处，顶点数（2个三角形有6个顶点）
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
        //绘制分割线
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
        //绘制木槌的点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);


    }
}
