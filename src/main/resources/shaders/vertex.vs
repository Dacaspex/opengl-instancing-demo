#version 330

layout (location=0) in vec3 inPosition;
layout (location=1) in mat4 modelViewMatrix;

// uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

out vec3 modelPosition;

void main()
{
    modelPosition = inPosition;
    gl_Position = projectionMatrix * modelViewMatrix * vec4(inPosition, 1.0);
}