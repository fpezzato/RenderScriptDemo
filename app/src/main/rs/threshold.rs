#pragma version(1)
#pragma rs java_package_name(com.example.android.renderscriptintrinsic)
#pragma rs_fp_relaxed


float thresholdValue;
static const float3 RED = { 1.0, 0.0, 0.0 };
static const float3 LUMINANCE_VECTOR = { 0.2125, 0.7154, 0.0721 };

//this is the Kernel's Root
uchar4 __attribute__((kernel)) filter(const uchar4 in, uint32_t x, uint32_t y) {

    float4 pixel = rsUnpackColor8888(in);

    //https://developer.android.com/guide/topics/renderscript/reference/rs_vector_math.html#android_rs:dot
    // 'dot' = Dot product of two vectors

    float luminance = dot(LUMINANCE_VECTOR, pixel.rgb);

    if (luminance < thresholdValue) {
        pixel.rgb = RED;
    } else {
        //pixel.rgb =  dot(pixel.rgb, LUMINANCE_VECTOR);
    }
    return rsPackColorTo8888(pixel);
}


