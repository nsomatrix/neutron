#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D uTexture;
uniform vec4 uDiffuseColor;
uniform float uAlphaThreshold;
uniform int uBlendingMode;

varying vec2 vTexCoord;
varying vec3 vNormal;
varying vec4 vColor;

void main() {
    vec4 texColor = texture2D(uTexture, vTexCoord);
    vec4 finalColor = texColor * uDiffuseColor * vColor;

    if (finalColor.a < uAlphaThreshold) {
        discard;
    }

    gl_FragColor = finalColor;
}

