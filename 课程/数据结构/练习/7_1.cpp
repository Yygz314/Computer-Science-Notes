#include <cstdio>
#include <cstring>
#include <cstdlib>
using namespace std;

#define OK 1
#define ERROR 0
#define OVERFLOW -1
#define MAXSTRLEN 255 

int SScount[36] = {0}; 

// 字符串结构体
typedef struct {
    char* ch;    
    int length;  
} HString;


void StrAssign(HString& T, const char* s) {
    if (T.ch != NULL) {
        delete[] T.ch;
        T.ch = NULL;
        T.length = 0;
    }
    // 分配新空间
    T.length = strlen(s);
    T.ch = new char[T.length + 1]; 
    if (T.ch == NULL) {
        exit(OVERFLOW);
    }
    strcpy(T.ch, s); // 复制字符串
}

void SS_count(const HString& T) {
    for (int i = 0; i < T.length; i++) {
        char ch = T.ch[i];
        if (ch >= 'a' && ch <= 'z') {
            SScount[ch - 'a']++; 
        } else if (ch >= 'A' && ch <= 'Z') {
            SScount[ch - 'A']++; 
        } else if (ch >= '0' && ch <= '9') {
            SScount[ch - '0' + 26]++; 
        }else{
            continue;
        }
    }
}

void PrintToConsole() {
    printf("\n字符频率统计\n");
    for (int i = 0; i < 26; i++) {
        if (SScount[i] != 0) {
            printf("%c: %d次\n", 'a' + i, SScount[i]);
        }
    }
    for (int i = 26; i < 36; i++) {
        if (SScount[i] != 0) {
            printf("%c: %d次\n", '0' + (i - 26), SScount[i]);
        }
    }
}

//写入文件
void SaveToFile(const char* filename) {
    FILE* fp = fopen(filename, "w");
    if (fp == NULL) {
        perror("无法打开文件");
        return;
    }
    fprintf(fp, "字符频率统计\n");
    for (int i = 0; i < 26; i++) {
        if (SScount[i] != 0) {
            fprintf(fp, "%c: %d次\n", 'a' + i, SScount[i]);
        }
    }
    for (int i = 26; i < 36; i++) {
        if (SScount[i] != 0) {
            fprintf(fp, "%c: %d次\n", '0' + (i - 26), SScount[i]);
        }
    }
    fclose(fp);
    printf("结果已写入文件：%s\n", filename);
}

int main() {
    HString T;
    T.ch = NULL; 
    T.length = 0;
    char input[MAXSTRLEN] = {0}; 
    char c;
    int idx = 0;
    printf("请输入字符串（停止输入请输入#）：");
    while ((c = getchar()) != '#' && idx < MAXSTRLEN - 1) {
        input[idx++] = c;
    }
    input[idx] = '\0';
    if (idx == 0) {
        printf("未输入任何字符！\n");
        return ERROR;
    }
    StrAssign(T, input);
    SS_count(T);
    PrintToConsole();
    SaveToFile("result.txt");

    if (T.ch != NULL) {
        delete[] T.ch;
        T.ch = NULL;
    }
    return OK;
}