#include <jni.h>
#include <speex/speex.h>
#include <speex/speex_echo.h>
#include <speex/speex_preprocess.h>

SpeexEchoState *echo_state=NULL;
SpeexPreprocessState *preprocessState=NULL;

JNIEXPORT void JNICALL
Java_com_future_android_study_media_SpeexJNI_init(JNIEnv *env,jobject jobj,int frameSize,jint sampleRateInHz){
    int filterLength=sampleRateInHz*100/1000;
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
    jshort *rec=(*env)->GetShortArrayElements(env,input_frame,NULL);
    jshort *play=(*env)->GetShortArrayElements(env,echo_frame,NULL);
    jshort *out=(*env)->GetShortArrayElements(env,output_frame,NULL);
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


int dec_frame_size;
int enc_frame_size;

static SpeexBits ebits, dbits;
void *enc_state;
void *dec_state;

JNIEXPORT void JNICALL Java_com_future_android_study_media_SpeexJNI_open
        (JNIEnv *env, jobject obj, jint quality) {
    speex_bits_init(&ebits);
    speex_bits_init(&dbits);

    enc_state = speex_encoder_init(&speex_nb_mode);
    dec_state = speex_decoder_init(&speex_nb_mode);
    speex_encoder_ctl(enc_state, SPEEX_SET_QUALITY, &quality);
    speex_encoder_ctl(enc_state, SPEEX_GET_FRAME_SIZE, &enc_frame_size);
    speex_decoder_ctl(dec_state, SPEEX_GET_FRAME_SIZE, &dec_frame_size);
}

JNIEXPORT jint Java_com_future_android_study_media_SpeexJNI_encode
        (JNIEnv *env, jobject obj, jshortArray lin, jbyteArray encoded) {
    jshort buffer[enc_frame_size];
    jbyte output_buffer[enc_frame_size];
    int tot_bytes = 0;

    speex_bits_reset(&ebits);

    (*env)->GetShortArrayRegion(env,lin, 0, enc_frame_size, buffer);
    speex_encode_int(enc_state, buffer, &ebits);

    //env->GetShortArrayRegion(lin, offset, enc_frame_size, buffer);
    //speex_encode_int(enc_state, buffer, &ebits);

    tot_bytes = speex_bits_write(&ebits, (char *)output_buffer,
                                 enc_frame_size);
    (*env)->SetByteArrayRegion(env,encoded, 0, tot_bytes,
                            output_buffer);

    return (jint)tot_bytes;
}

JNIEXPORT jint JNICALL Java_com_future_android_study_media_SpeexJNI_decode
        (JNIEnv *env, jobject obj, jbyteArray encoded, jshortArray lin, jint size) {

    jbyte buffer[dec_frame_size];
    jshort output_buffer[dec_frame_size];
    jsize encoded_length = size;

    (*env)->GetByteArrayRegion(env,encoded, 0, encoded_length, buffer);
    speex_bits_read_from(&dbits, (char *)buffer, encoded_length);
    speex_decode_int(dec_state, &dbits, output_buffer);
    (*env)->SetShortArrayRegion(env,lin, 0, dec_frame_size,
                             output_buffer);

    return (jint)dec_frame_size;
}

JNIEXPORT jint JNICALL Java_com_future_android_study_media_SpeexJNI_getFrameSize
        (JNIEnv *env, jobject obj) {
    return (jint)enc_frame_size;

}

JNIEXPORT void JNICALL Java_com_future_android_study_media_SpeexJNI_close
        (JNIEnv *env, jobject obj) {
    if(enc_state) {
        speex_bits_destroy(&ebits);
        speex_encoder_destroy(enc_state);
        enc_state=NULL;
    }
    if(dec_state) {
        speex_bits_destroy(&dbits);
        speex_decoder_destroy(dec_state);
        dec_state=NULL;
    }
}