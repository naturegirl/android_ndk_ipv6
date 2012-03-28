/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define DEBUG

/* struct in6_addr */
#include <netinet/in.h>
/* getifaddrs, freeifaddrs */
#include <sys/types.h>

/* inet_ntop, inet_pton */
#include <arpa/inet.h>

#if defined(DEBUG)
#define DG(a, b...) printf("[%s][%s][%d]"a, __FILE__, __func__, __LINE__, ##b)
#else
#define DG(a, b...)
#endif

#include <android/log.h>

#define ER(a, b...) printf("[%s][%s][%d]"a, __FILE__, __func__, __LINE__, ##b)


/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/HelloJni/HelloJni.java
 */
 // call fileExists() first!
jstring
Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
    struct in6_addr addr6;
    char *ifname = "eth0";
    char address[64];
    char buf[1024];
	__android_log_write(ANDROID_LOG_DEBUG, "mee", "just here");
    get_ipv6addr(&addr6, buf);
    return (*env)->NewStringUTF(env, buf);
}

#define IF_INET6 "/proc/net/if_inet6"

// for jboolean 1 is true, 0 is false
jboolean
Java_com_example_hellojni_HelloJni_fileExists( JNIEnv* env, jobject thiz )
{
	FILE *fp;
	int val;
	if (NULL == (fp = fopen(IF_INET6, "r"))) {		// can open
		val = 0;
		__android_log_write(ANDROID_LOG_DEBUG, "mee", "can't read. fopen failed");
	}
	else {
		val = 1;
		fclose(fp);
	}
	
	return val;
}

int get_ipv6addr(struct in6_addr *addr6, char returnbuf[])
{

	char buf[256];
    char str[128], address[64];
    char *addr, *index, *prefix, *scope, *flags, *name;
    char *delim = " \t\n", *p, *q;
    FILE *fp;
    int count;
    
    if (!addr6) {
        __android_log_write(ANDROID_LOG_ERROR, "mee", "addr6 and iface can't be NULL!\n");
        return -1;
    }
    
    if (NULL == (fp = fopen(IF_INET6, "r"))) {
		__android_log_write(ANDROID_LOG_ERROR, "mee", "can't read. fopen failed");
        perror("fopen error");
        return -1;
    }
    
    while (fgets(str, sizeof(str), fp)) {
        DG("str:%s", str);
        addr = strtok(str, delim);
        index = strtok(NULL, delim);
        prefix = strtok(NULL, delim);
        scope = strtok(NULL, delim);
        flags = strtok(NULL, delim);
        name = strtok(NULL, delim);
		sprintf(buf, "addr:%s, index:0x%s, prefix:0x%s, scope:0x%s, flags:0x%s, name:%s\n",
           addr, index, prefix, scope, flags, name);
		//__android_log_write(ANDROID_LOG_DEBUG, "mee", buf);
        
        memset(address, 0x00, sizeof(address));
        p = addr;
        q = address;
        count = 0;
        while (*p != ' ') {
            if (count == 4) {
                *q++ = ':';
                count = 0;
            }
            *q++ = *p++;
            count++;
        }
		sprintf(buf, "%s:%s;", name, addr);		// this is the required format
		strcat(returnbuf, buf);
		//__android_log_write(ANDROID_LOG_DEBUG, "mee", buf);
        
        inet_pton(AF_INET6, address, addr6);
    }
	__android_log_write(ANDROID_LOG_INFO, "mee", returnbuf);
#undef IPV6_ADDR_LINKLOCAL
    
    fclose(fp);
    return 0;
}