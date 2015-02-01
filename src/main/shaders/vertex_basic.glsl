#version 330
layout(location = 0) in vec4 position;
layout(location = 1) in vec4 color;
smooth out vec4 theColor;
uniform vec2 offset;
void main()
{
  vec4 totalOffset = vec4(offset.x, offset.y, 0.0, 0.0);
  gl_Position = position + totalOffset;
  theColor = color;
}