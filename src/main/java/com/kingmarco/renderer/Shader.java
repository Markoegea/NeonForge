package com.kingmarco.renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

/**
 * This class represents a Shader logic in OpenGL to be processed, rendered and displayed.
 */
public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    /**
     * Constructs a new Shader object and initializes it with the shader code from the specified file.
     *
     * @param filepath The path to the file containing the shader code.
     */
    public Shader(String filepath){
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find the second pattern after #type 'pattern
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")){
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")){
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }

        } catch (IOException e){
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }
    }

    /**
     * Compiles the vertex and fragment shaders.
     * It first loads and compiles the vertex shader, checks for errors, and then does the same for the fragment shader.
     */
    public void compile(){
        // ========================================
        // Compile Shaders
        // ========================================
        int vertexID, fragmentID;

        //First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '"+ filepath +"'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        //First load and compile the vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '"+ filepath +"'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        link(vertexID, fragmentID, success);
    }

    /**
     * Links the compiled vertex and fragment shaders to create a complete shader program.
     * It also checks for errors during the linking process.
     *
     * @param vertexID The ID of the compiled vertex shader.
     * @param fragmentID The ID of the compiled fragment shader.
     * @param success The status of the shader compilation process.
     */
    public void link(int vertexID, int fragmentID, int success){
        // ========================================
        // Link Shaders
        // ========================================

        // Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultShader.glsl'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }


    /**
     * Binds the shader program if it is not already being used.
     */
    public void use() {
        if (!beingUsed){
            // Bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    /**
     * Detaches the shader program.
     */
    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }

    /**
     * Uploads a 4x4 matrix to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param mat4 The 4x4 matrix to upload.
     */
    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    /**
     * Uploads a 3x3 matrix to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param mat3 The 3x3 matrix to upload.
     */
    public void uploadMat3f(String varName, Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    /**
     * Uploads a 4D vector to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param vec The 4D vector to upload.
     */
    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    /**
     * Uploads a 3D vector to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param vec The 3D vector to upload.
     */
    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    /**
     * Uploads a 2D vector to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param vec The 2D vector to upload.
     */
    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    /**
     * Uploads a float value to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param val The float value to upload.
     */
    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    /**
     * Uploads an integer value to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param val The integer value to upload.
     */
    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    /**
     * Uploads a texture to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param slot The slot of the texture to upload.
     */
    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    /**
     * Uploads an array of integers to the shader.
     *
     * @param varName The name of the variable in the shader.
     * @param array The array of integers to upload.
     */
    public void uploadIntArray(String varName, int[] array){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}
