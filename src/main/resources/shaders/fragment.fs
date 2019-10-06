#version 330

in vec3 modelPosition;

out vec4 fragColor;

void main()
{
    if (modelPosition.x >= 0.99) {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    } else if (modelPosition.x <= -0.99) {
        fragColor = vec4(0.0, 1.0, 0.0, 1.0);
    } else if (modelPosition.y >= 0.99) {
        fragColor = vec4(0.0, 0.0, 1.0, 1.0);
    } else if (modelPosition.y <= -0.99) {
        fragColor = vec4(1.0, 1.0, 0.5, 1.0);
    } else if (modelPosition.z >= 0.99) {
        fragColor = vec4(0.0, 1.0, 1.0, 1.0);
    } else if (modelPosition.z <= -0.99) {
        fragColor = vec4(1.0, 0.0, 1.0, 1.0);
    } else {
        fragColor = vec4(0.0, 0.0, 0.5, 1.0);
    }
}