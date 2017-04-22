/*
 * importdex.cpp
 *
 *  Created on: 2014年6月24日
 *      Author: boyliang
 */

#include <stdio.h>
#include <stddef.h>
//#include <jni.h>
#include <android_runtime/AndroidRuntime.h>

#include "log.h"
#include "importdex.h"

using namespace android;

static const char JSTRING[] = "Ljava/lang/String;";
static const char JCLASS_LOADER[] = "Ljava/lang/ClassLoader;";
static const char JCLASS[] = "Ljava/lang/Class;";
static const char JFILE[] = "Ljava/io/File;";

static JNIEnv* jni_env;
static char sig_buffer[512];

//EntryClass entryClass;

//ClassLoader.getSystemClassLoader()
static jobject getSystemClassLoader(){

	LOGI("getSystemClassLoader is Executing!!");

	jclass class_loader_claxx = jni_env->FindClass("java/lang/ClassLoader");
	snprintf(sig_buffer, 512, "()%s", JCLASS_LOADER);

	LOGI("sig_buffer is %s",sig_buffer);

	jmethodID getSystemClassLoader_method = jni_env->GetStaticMethodID(class_loader_claxx, "getSystemClassLoader", sig_buffer);

	LOGI("getSystemClassLoader is finished!!");

	return jni_env->CallStaticObjectMethod(class_loader_claxx, getSystemClassLoader_method);

}

static bool jni_exception(){
   if(jni_env->ExceptionCheck())
	{
	   jthrowable exc;
       exc = jni_env->ExceptionOccurred();
	   jboolean isCopy = false;
	   jni_env->ExceptionClear();
       jmethodID toString = jni_env->GetMethodID(jni_env->FindClass("java/lang/Object"), "toString", "()Ljava/lang/String;");
       jstring s = (jstring)jni_env->CallObjectMethod(exc, toString);
       const char* utf = jni_env->GetStringUTFChars(s, &isCopy);
       LOGE("ExceptionOccurred:%s",utf);
       return true;
	}
	return false;
}

jobject getGlobalContext(JNIEnv *env) {
	jclass activityThread = env->FindClass("android/app/ActivityThread");

	jmethodID currentActivityThread = env->GetStaticMethodID(activityThread, "currentActivityThread", "()Landroid/app/ActivityThread;");

	jobject at = env->CallStaticObjectMethod( activityThread, currentActivityThread);

	jmethodID getApplication = env->GetMethodID(activityThread, "getApplication", "()Landroid/app/Application;");

	jobject context = env->CallObjectMethod(at, getApplication);

	return context;
}

// context.getDir("dex", 0);

#define ROOTDIR "/data/inject"


__attribute__ ((__constructor__))
void _init(){
	LOGI("importdex dll _init");
}

void callback(char* param) {
    LOGI("param=%s", param);
    char* path = param;
    if (param == NULL) {
        path = ROOTDIR "/inject.apk";
    }
    path = ROOTDIR "/inject.apk";
	LOGI("Main is Executing!!");
	JavaVM* jvm = AndroidRuntime::getJavaVM();
	LOGI("jvm is %p",jvm);

	JavaVMAttachArgs args = {JNI_VERSION_1_4, NULL, NULL};
	jvm->AttachCurrentThread(&jni_env, (void*) &args);
	//TODO 使用JNIEnv

	// jvm->DetachCurrentThread();

	LOGI("jni_env is %p", jni_env);
  LOGI("path=%s", path);

	jobject context = getGlobalContext(jni_env);
	jclass context_claxx = jni_env->FindClass("android/content/Context");
	snprintf(sig_buffer, 512, "(%s%s)%s", JSTRING, "I", JFILE);
	jmethodID getDir_method = jni_env->GetMethodID(context_claxx, "getDir", sig_buffer);
	jstring js_dex = jni_env->NewStringUTF("dex");
	jobject dex_out_file = jni_env->CallObjectMethod(context, getDir_method, js_dex, 0);
	jclass file_claxx = jni_env->FindClass("java/io/File");
	snprintf(sig_buffer, 512, "()%s", JSTRING);
	jmethodID getPath_method = jni_env->GetMethodID(file_claxx, "getAbsolutePath", sig_buffer);
	jstring dex_out_path = (jstring)jni_env->CallObjectMethod(dex_out_file, getPath_method);


	jstring apk_path = jni_env->NewStringUTF(ROOTDIR "/inject.apk");
	// jstring dex_out_path = jni_env->NewStringUTF(ROOTDIR);
	jclass dexloader_claxx = jni_env->FindClass("dalvik/system/DexClassLoader");

	// LOGI("apk_path:%s",apk_path);
	// LOGI("dex_out_path:%s",dex_out_path);

	snprintf(sig_buffer, 512, "(%s%s%s%s)V", JSTRING, JSTRING, JSTRING, JCLASS_LOADER);
	LOGI("sig_buffer is %s",sig_buffer);
	jmethodID dexloader_init_method = jni_env->GetMethodID(dexloader_claxx, "<init>", sig_buffer);

	snprintf(sig_buffer, 512, "(%s)%s", JSTRING, JCLASS);

	LOGI("sig_buffer is %s",sig_buffer);

	jmethodID loadClass_method = jni_env->GetMethodID(dexloader_claxx, "loadClass", sig_buffer);
    jobject nullObj;
	jobject class_loader = getSystemClassLoader();
    LOGI("getSystemClassLoader %p", class_loader);
	// if(JNI_TRUE == jni_env->IsSameObject(class_loader, nullObj)){
	  // LOGI("Failed GetClassLoader");
	// }else{
	   // LOGI("Succeeded GetClassLoader");
	// }
	//check_value(class_loader);


	jobject dex_loader_obj = jni_env->NewObject(dexloader_claxx, dexloader_init_method, apk_path, dex_out_path, NULL, class_loader);
	if(jni_exception()){
	  return;
	}
	LOGI("step---1");
	// if(JNI_TRUE == jni_env->IsSameObject(dex_loader_obj, nullObj)){
 //    	  LOGI("Failed dex_loader_obj");
 //    	}else{
 //    	   LOGI("Succeeded dex_loader_obj");
 //    	}
	jstring class_name = jni_env->NewStringUTF("com.demo.inject2.EntryClass");
	jclass entry_class = static_cast<jclass>(jni_env->CallObjectMethod(dex_loader_obj, loadClass_method, class_name));
	if(jni_exception()){
      return;
    }
	LOGI("step---2");
	LOGI("jni_env:%p",jni_env);
	LOGI("step---2-1");
	//LOGI("entry_class:%s",entry_class);
	jmethodID invoke_method = jni_env->GetStaticMethodID(entry_class, "invoke", "(I)[Ljava/lang/Object;");
    if(jni_exception()){
      return;
    }
	//check_value(invoke_method);
	LOGI("step---3");
	jobjectArray objectarray = (jobjectArray) jni_env->CallStaticObjectMethod(entry_class, invoke_method, 0);
	LOGI("step---4");
	// jvm->DetachCurrentThread();

	LOGI("Main is finished");

}
