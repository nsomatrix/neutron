package org.neutron.graphics3d.opengl;

import org.lwjgl.opengl.GL20;
import org.neutron.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GLShader {
    private final int programId;

    public GLShader(String vertexResource, String fragmentResource) {
        String vSource = loadResource(vertexResource);
        String fSource = loadResource(fragmentResource);

        int vShader = compileShader(GL20.GL_VERTEX_SHADER, vSource);
        int fShader = compileShader(GL20.GL_FRAGMENT_SHADER, fSource);

        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vShader);
        GL20.glAttachShader(programId, fShader);
        GL20.glLinkProgram(programId);

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(programId);
            Logger.error("Failed to link GLSL program (" + vertexResource + "): " + log);
            GL20.glDeleteShader(vShader);
            GL20.glDeleteShader(fShader);
            throw new RuntimeException("Failed to link GLSL program (" + vertexResource + "): " + log);
        }

        GL20.glDeleteShader(vShader);
        GL20.glDeleteShader(fShader);
    }

    public void use() {
        GL20.glUseProgram(programId);
    }

    public int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(programId, name);
    }

    public int getAttribLocation(String name) {
        return GL20.glGetAttribLocation(programId, name);
    }

    public int getProgramId() {
        return programId;
    }

    public void delete() {
        GL20.glDeleteProgram(programId);
    }

    private static int compileShader(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader);
            Logger.error("GLSL compile error: " + log);
            GL20.glDeleteShader(shader);
            throw new RuntimeException("GLSL compile error: " + log);
        }
        return shader;
    }

    private static String loadResource(String path) {
        try (InputStream is = GLShader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Shader resource not found: " + path);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read;
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
            return new String(baos.toByteArray(), "UTF-8");
        } catch (Exception e) {
            Logger.error("Could not load shader: " + path, e);
            return "";
        }
    }
}

