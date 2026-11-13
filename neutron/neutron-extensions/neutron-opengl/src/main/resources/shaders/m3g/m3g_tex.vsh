uniform mat4 uMatrix;
uniform mat4 uTexMatrix;
uniform vec2 uTexSize;

attribute vec4 aPosition;
attribute vec3 aNormal;
attribute vec2 aTexCoord;
attribute vec4 aColor;

varying vec2 vTexCoord;
varying vec3 vNormal;
varying vec4 vColor;

void main() {
    gl_Position = uMatrix * aPosition;
    vNormal = aNormal;
    vColor = aColor;
    
    vec4 tc = uTexMatrix * vec4(aTexCoord, 0.0, 1.0);
    vTexCoord = (tc.xy) / uTexSize;
}

