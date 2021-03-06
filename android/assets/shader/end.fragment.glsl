#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;

uniform vec4 u_diffuseColor0;
uniform vec4 u_diffuseColor1;

uniform float u_animationMask;
uniform float u_animationStartTime;
uniform float u_animationDuration;

varying vec2 v_uv;

#define OUTLINE_WIDTH 0.02

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
{
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
		+ i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

void main()
{
    vec4 diffuse = vec4(0.0);

    float mid = 0.5 + snoise(vec2(0.0, v_uv.y + u_time / 2.0)) / 2.0;

    diffuse.rgb += mix(
        u_diffuseColor0.rgb,
        vec3(0.0, 0.0, 0.0),
        (v_uv.x - mid) * 10.0
    );

    diffuse.rgb += mix(
        u_diffuseColor1.rgb,
        vec3(0.0, 0.0, 0.0),
        (mid - v_uv.x) * 10.0
    );

    float distance = distance(vec2(0.5, 0.5), v_uv);

    float externalThresholdDistance = mix(
        0.5,
        0.001,
        u_animationMask * clamp((u_time - u_animationStartTime) / u_animationDuration, 0.0, 1.0)
    );

    if (distance > externalThresholdDistance)
        discard;

    float internalThresholdDistance = externalThresholdDistance - OUTLINE_WIDTH;

    if (distance > internalThresholdDistance)
    {
        diffuse = vec4(vec3(0.3), 0.5);
    }
    else
    {
        diffuse.a = 1.0;
    }

    gl_FragColor = diffuse;
}
