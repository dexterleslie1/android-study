#include <jni.h>
#include <speex/speex_echo.h>
#include <speex/speex_preprocess.h>

SpeexEchoState *echo_state=NULL;
SpeexPreprocessState *preprocessState=NULL;

JNIEXPORT void JNICALL
Java_com_future_android_study_media_SpeexJNI_init(JNIEnv *env,jobject jobj,jint sampleRateInHz){
    int frameSize=1000;
    int filterLength=sampleRateInHz*250/1000;
    echo_state=speex_echo_state_init(frameSize, filterLength);
    speex_echo_ctl(echo_state, SPEEX_ECHO_SET_SAMPLING_RATE, &sampleRateInHz);
    preprocessState=speex_preprocess_state_init(frameSize, sampleRateInHz);
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_ECHO_STATE, echo_state);
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_AGC ,echo_state);
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_DENOISE ,echo_state);
}

JNIEXPORT jshortArray JNICALL
Java_com_future_android_study_media_SpeexJNI_cancellation(
        JNIEnv *env, jobject jObj, jshortArray input_frame, jshortArray echo_frame){

    //create native shorts from java shorts
    jshort *native_input_frame = (*env)->GetShortArrayElements(env, input_frame, NULL);
    jshort *native_echo_frame = (*env)->GetShortArrayElements(env, echo_frame, NULL);

    //allocate memory for output data
    jint length = (*env)->GetArrayLength(env, input_frame);
    jshortArray temp = (*env)->NewShortArray(env, length);
    jshort *native_output_frame = (*env)->GetShortArrayElements(env, temp, 0);

    //call echo cancellation
    speex_echo_cancellation(echo_state, native_input_frame, native_echo_frame, native_output_frame);
    speex_preprocess_run(preprocessState, native_output_frame);

    //convert native output to java layer output
    jshortArray output_shorts = (*env)->NewShortArray(env, length);
    (*env)->SetShortArrayRegion(env, output_shorts, 0, length, native_output_frame);

    //cleanup and return
    (*env)->ReleaseShortArrayElements(env, input_frame, native_input_frame, 0);
    (*env)->ReleaseShortArrayElements(env, echo_frame, native_echo_frame, 0);
    (*env)->ReleaseShortArrayElements(env, temp, native_output_frame, 0);
    return output_shorts;
}

JNIEXPORT void JNICALL
Java_com_future_android_study_media_SpeexJNI_destroy(JNIEnv *env,jobject jobj){
    if(echo_state){
        speex_echo_state_destroy(echo_state);
    }
    if(preprocessState){
        speex_preprocess_state_destroy(preprocessState);
    }
}