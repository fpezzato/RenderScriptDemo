#pragma version(1)
#pragma rs java_package_name(com.example.android.renderscriptintrinsic)
#pragma rs_fp_relaxed


float thresholdValue;
static const float3 BLACK = { 0.0, 0.0, 0.0 };
static const float3 COLOR = { 1.0, 0.0, 0.0 };
static const float3 LUMINANCE_VECTOR = { 0.2125, 0.7154, 0.0721 };

//this is the Kernel's Root
uchar4 __attribute__((kernel)) filter(const uchar4 in, uint32_t x, uint32_t y) {
    float4 pixel = rsUnpackColor8888(in);
    float luminance = dot(LUMINANCE_VECTOR, pixel.rgb);
    //rsDebug("===========YAY==================",luminance );
    //rsDebug("=                         ======",thresholdValue );
    if (luminance < 0.3) {
        pixel.rgb = COLOR;
    }else{

    }
    return rsPackColorTo8888(pixel);
}


