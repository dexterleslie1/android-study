#include <jni.h>
#include <speex/speex_echo.h>
#include <speex/speex_preprocess.h>

SpeexEchoState *echo_state=NULL;
SpeexPreprocessState *preprocessState=NULL;

JNIEXPORT void JNICALL
Java_com_future_android_study_media_SpeexJNI_init(JNIEnv *env,jobject jobj,jint sampleRateInHz){
    int frameSize=sampleRateInHz*20/1000;
    int filterLength=sampleRateInHz*128/1000;
    echo_state=speex_echo_state_init(frameSize, filterLength);
    speex_echo_ctl(echo_state, SPEEX_ECHO_SET_SAMPLING_RATE, &sampleRateInHz);
    preprocessState=speex_preprocess_state_init(frameSize, sampleRateInHz);
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_ECHO_STATE, echo_state);
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_AGC ,echo_state);
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_DENOISE ,echo_state);
}

JNIEXPORT void JNICALL
Java_com_future_android_study_media_SpeexJNI_cancellation(
                                        JNIEnv *env,
                                        jobject jobj,
                                        jshortArray input_frame,
                                        jshortArray echo_frame,
                                        jshortArray output_frame){
    jboolean iscopy1,iscopy2,iscopy3;
    jshort *rec=(*env)->GetShortArrayElements(env,input_frame,&iscopy1);
    jshort *play=(*env)->GetShortArrayElements(env,echo_frame,&iscopy2);
    jshort *out=(*env)->GetShortArrayElements(env,output_frame,&iscopy3);
    speex_echo_cancellation(echo_state, rec, play, out);
    speex_preprocess_run(preprocessState, out);
    (*env)->ReleaseShortArrayElements(env,input_frame,rec,0);
    (*env)->ReleaseShortArrayElements(env,echo_frame,play,0);
    (*env)->ReleaseShortArrayElements(env,output_frame,out,0);
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