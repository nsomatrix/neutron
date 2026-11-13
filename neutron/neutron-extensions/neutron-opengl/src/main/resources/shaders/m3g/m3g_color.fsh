#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 uDiffuseColor;
uniform float uAlphaThreshold;

varying vec4 vColor;
varying vec3 vNormal;

void main() {
    vec4 finalColor = uDiffuseColor * vColor;
    if (finalColor.a < uAlphaThreshold) {
        discard;
    }
    gl_FragColor = finalColor;
}

