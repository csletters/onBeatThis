package sheetRenderer;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.json.JSONException;

import com.example.onbeatthis.R;


import shapes.Square;

import loaders.RawResourceReader;
import loaders.ShaderHandles;
import loaders.ShaderHelper;
import loaders.TextureHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyRenderer implements Renderer {

	int widthView,heightView;
	Context mActivityContext;

	float[] projection = new float[16];
	float[] view = new float[16];
	float[] model = new float[16];
	float[] rotMatrix = new float[16];
	float[] mv = new float[16];
	float ratio;
	int[] previewTexture;
	int[] defaultTexture,lineTexture;
	float widthCordsRadiusPerScreen,heightCordsRadiusPerScreen;
	float ytranslation;
	double cordsPerSec;
	double intervalDistance;
	float startTime = 0;
	float elapsedTime = 0.0f;
	float translation =0.0f;
	int maxScroll = 0;
	int scrollPosition = 0;
	boolean isPlaying = false;
	
	Square object;
	ShaderHandles squareObjectRenderer;
	
	public MyRenderer(final Context activityContext)
	{
		mActivityContext = activityContext;
	}
	@Override
	public void onDrawFrame(GL10 gl) {
		timer();
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		GLES20.glUseProgram(squareObjectRenderer.programHandle);
		Matrix.scaleM(model, 0,widthCordsRadiusPerScreen*2, widthCordsRadiusPerScreen*1.5f, 1.0f);
		Matrix.translateM(model, 0, -0.5f, -0.5f, 0.0f);
		GLES20.glEnable(GLES20.GL_BLEND);
		object.draw(projection, view, model, squareObjectRenderer, defaultTexture[0]);
		GLES20.glDisable(GLES20.GL_BLEND);
		Matrix.setIdentityM(model, 0);
		
		if(isPlaying)
		{
			Matrix.translateM(model, 0, -widthCordsRadiusPerScreen+translation, ytranslation, 1.0f);
			Matrix.scaleM(model, 0, 0.05f, 3.5f, 1.0f);
			Matrix.rotateM(model, 0, 90, 0, 0, 1);
			Matrix.translateM(model, 0, -0.5f, -0.5f, 0.0f);
			object.draw(projection, view, model, squareObjectRenderer, lineTexture[0]);
			Matrix.setIdentityM(model, 0);
		}
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		ratio = (float)width/height;
		Matrix.perspectiveM(projection, 0, 90.0f, ratio, 0.1f, 600f);
		Matrix.setLookAtM(view, 0, 0.0f, 0.0f, 10, 0, 0.0f, 0, 0, 1, 0);
		Matrix.setIdentityM(model, 0);
		widthView = width;
		heightView = height;
		// world cords radius displayed per screen
		widthCordsRadiusPerScreen = (float) (ratio * Math.tanh(45) * 10);
		cordsPerSec = (widthCordsRadiusPerScreen*2)/15.0;
		intervalDistance = cordsPerSec/10.0;
		translation = 0.0f;
		ytranslation = 3.5f;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GLES20.glClearDepthf(1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);
		GLES20.glDepthMask(true);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		// create shader program for objects with basic textures and lighting
		squareObjectRenderer = new ShaderHandles();
		squareObjectRenderer.programHandle = createShader(R.raw.vertex, R.raw.fragment);
		initBasicHandles(squareObjectRenderer);
		
		defaultTexture = TextureHelper.loadTexture(mActivityContext, R.drawable.sheetmusic);
		lineTexture = TextureHelper.loadTexture(mActivityContext, R.drawable.linecolor);
		
		object = new Square();
	}
	
	
	public int createShader(int vertex, int fragment) {
		String vertexShaderCode = RawResourceReader.readTextFileFromRawResource(mActivityContext, vertex);
		String fragmentShaderCode = RawResourceReader.readTextFileFromRawResource(mActivityContext, fragment);

		int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		int mProgram;

		mProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle,fragmentShaderHandle);

		return mProgram;
	}

	public void initBasicHandles( ShaderHandles shader) {
		// attributes
		shader.positionHandle = GLES20.glGetAttribLocation(shader.programHandle,"aPosition");
		shader.mTextureCoordinateHandle = GLES20.glGetAttribLocation(shader.programHandle, "aTexCord");

		// uniforms
		shader.modelHandle = GLES20.glGetUniformLocation(shader.programHandle, "model");
		shader.viewHandle = GLES20.glGetUniformLocation(shader.programHandle, "view");
		shader.projectionHandle = GLES20.glGetUniformLocation(shader.programHandle,	"projection");
		shader.mTextureUniformHandle.add(GLES20.glGetUniformLocation(shader.programHandle, "uTexture"));
	}

	public void timer() {
		float diff = ((System.nanoTime() - startTime) / 1000000000.0f);
		startTime = System.nanoTime();
		elapsedTime += diff;
		if (elapsedTime > 0.1) {
			elapsedTime = 0;
			translation += intervalDistance;
			if(translation >widthCordsRadiusPerScreen*2)
			{
				translation =0.0f;
				ytranslation -= 3.5;
				if(ytranslation < 0)
					intervalDistance = (widthCordsRadiusPerScreen*2)/100.0;
				if(ytranslation < -3.5)
					isPlaying = false;
			}
			
		}

	}
	public void playing(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
		translation =0.0f;
		ytranslation = 3.5f;
		intervalDistance = (widthCordsRadiusPerScreen*2)/150.0;
	}
	
}
