uniform mat4 uMatrix;

attribute vec4 aPosition;
attribute vec4 aColor;
attribute vec3 aNormal;

varying vec4 vColor;
varying vec3 vNormal;

void main() {
    gl_Position = uMatrix * aPosition;
    vColor = aColor;
    vNormal = aNormal;
}

