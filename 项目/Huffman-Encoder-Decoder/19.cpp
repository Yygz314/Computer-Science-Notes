#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_NODE 512
#define MAX_CHAR 256
#define MAX_CODE 128

typedef struct {
    unsigned int weight;
    int parent, lchild, rchild;
    char ch;
} HuffmanNode;

typedef struct {
    char ch;
    char code[MAX_CODE];
} HuffmanCode;

HuffmanNode hfmTree[MAX_NODE];
HuffmanCode codeTable[MAX_CHAR];
int leafNum;

//选择函数
void selectMin(int n, int *s1, int *s2) {
    unsigned int min1 = 0xffffffff, min2 = 0xffffffff;
    *s1 = *s2 = -1;
    for (int i = 0; i < n; i++) {
        if (hfmTree[i].parent == -1) {
            if (hfmTree[i].weight < min1) {
                min2 = min1; *s2 = *s1;
                min1 = hfmTree[i].weight; *s1 = i;
            } else if (hfmTree[i].weight < min2) {
                min2 = hfmTree[i].weight; *s2 = i;
            }
        }
    }
}

//初始化
void initHuffman() {
    char treeFile[100];
    printf("请输入哈夫曼树文件名：");
    scanf("%s", treeFile);

    FILE *fp = fopen(treeFile, "wb");
    if (!fp) return;

    printf("请输入字符集大小 n：");
    scanf("%d", &leafNum);

    int totalNode = 2 * leafNum - 1;
    for (int i = 0; i < totalNode; i++) {
        hfmTree[i].parent = hfmTree[i].lchild = hfmTree[i].rchild = -1;
        hfmTree[i].weight = 0;
        hfmTree[i].ch = '\0';
    }

    for (int i = 0; i < leafNum; i++) {
        printf("输入第%d个字符和权值（字符 权值）：", i + 1);
        char ch;
        do {
            ch = getchar();
        } while (ch == '\n');   // 跳过回车

        hfmTree[i].ch = ch;
        scanf("%u", &hfmTree[i].weight);
        getchar();              // 吃掉行末回车
    }

    for (int i = leafNum; i < totalNode; i++) {
        int s1, s2;
        selectMin(i, &s1, &s2);
        hfmTree[s1].parent = hfmTree[s2].parent = i;
        hfmTree[i].lchild = s1;
        hfmTree[i].rchild = s2;
        hfmTree[i].weight = hfmTree[s1].weight + hfmTree[s2].weight;
    }

    fwrite(&leafNum, sizeof(int), 1, fp);
    fwrite(hfmTree, sizeof(HuffmanNode), totalNode, fp);
    fclose(fp);

    printf("哈夫曼树建立完成。\n");
}

//编码表
void buildCodeTable(char *treeFile) {
    FILE *fp = fopen(treeFile, "rb");
    fread(&leafNum, sizeof(int), 1, fp);
    int totalNode = 2 * leafNum - 1;
    fread(hfmTree, sizeof(HuffmanNode), totalNode, fp);
    fclose(fp);

    for (int i = 0; i < leafNum; i++) {
        char tmp[MAX_CODE];
        int idx = MAX_CODE - 1;
        tmp[idx] = '\0';
        int c = i, p = hfmTree[i].parent;
        while (p != -1) {
            tmp[--idx] = (hfmTree[p].lchild == c) ? '0' : '1';
            c = p;
            p = hfmTree[c].parent;
        }
        codeTable[i].ch = hfmTree[i].ch;
        strcpy(codeTable[i].code, &tmp[idx]);
    }
}

//编码
void encode() {
    char treeFile[100], srcFile[100], codeFile[100];
    printf("输入哈夫曼树文件名：");
    scanf("%s", treeFile);
    printf("输入待编码文件名：");
    scanf("%s", srcFile);
    printf("输入编码文件名：");
    scanf("%s", codeFile);

    buildCodeTable(treeFile);

    FILE *fin = fopen(srcFile, "r");
    FILE *fout = fopen(codeFile, "wb");

    unsigned char buf = 0;
    int bitCount = 0;
    int totalBits = 0;
    int ch;

    /* 先占位写入 totalBits */
    fwrite(&totalBits, sizeof(int), 1, fout);

    while ((ch = fgetc(fin)) != EOF) {
        for (int i = 0; i < leafNum; i++) {
            if (codeTable[i].ch == ch) {
                char *p = codeTable[i].code;
                while (*p) {
                    buf <<= 1;
                    if (*p == '1') buf |= 1;
                    bitCount++;
                    totalBits++;

                    if (bitCount == 8) {
                        fwrite(&buf, 1, 1, fout);
                        buf = 0;
                        bitCount = 0;
                    }
                    p++;
                }
                break;
            }
        }
    }

    if (bitCount > 0) {
        buf <<= (8 - bitCount);
        fwrite(&buf, 1, 1, fout);
    }

    /* 回到文件头，写入真正的 totalBits */
    fseek(fout, 0, SEEK_SET);
    fwrite(&totalBits, sizeof(int), 1, fout);

    fclose(fin);
    fclose(fout);
    printf("编码完成。\n");
}

//译码
void decode() {
    char treeFile[100], codeFile[100], outFile[100];
    printf("输入哈夫曼树文件名：");
    scanf("%s", treeFile);
    printf("输入编码文件名：");
    scanf("%s", codeFile);
    printf("输入译码输出文件名：");
    scanf("%s", outFile);

    FILE *fpTree = fopen(treeFile, "rb");
    fread(&leafNum, sizeof(int), 1, fpTree);
    int totalNode = 2 * leafNum - 1;
    fread(hfmTree, sizeof(HuffmanNode), totalNode, fpTree);
    fclose(fpTree);

    FILE *fin = fopen(codeFile, "rb");
    FILE *fout = fopen(outFile, "w");

    int totalBits;
    fread(&totalBits, sizeof(int), 1, fin);

    unsigned char byte;
    int p = totalNode - 1;
    int usedBits = 0;

    while (fread(&byte, 1, 1, fin) == 1) {
        for (int i = 7; i >= 0; i--) {
            if (usedBits == totalBits) break;

            int bit = (byte >> i) & 1;
            usedBits++;

            p = bit ? hfmTree[p].rchild : hfmTree[p].lchild;
            if (hfmTree[p].lchild == -1) {
                fputc(hfmTree[p].ch, fout);
                p = totalNode - 1;
            }
        }
        if (usedBits == totalBits) break;
    }

    fclose(fin);
    fclose(fout);
    printf("译码完成。\n");
}

//打印编码文件
void printCode() {
    char codeFile[100], outFile[100];
    printf("输入编码文件名：");
    scanf("%s", codeFile);
    printf("输入输出文件名：");
    scanf("%s", outFile);

    FILE *fin = fopen(codeFile, "rb");
    FILE *fout = fopen(outFile, "w");

    unsigned char byte;
    int count = 0;

    while (fread(&byte, 1, 1, fin) == 1) {
        for (int i = 7; i >= 0; i--) {
            char bit = ((byte >> i) & 1) ? '1' : '0';
            printf("%c", bit);
            fputc(bit, fout);
            count++;

            if (count == 50) {
                printf("\n");
                fputc('\n', fout);
                count = 0;
            }
        }
    }

    if (count != 0) {
        printf("\n");
        fputc('\n', fout);
    }

    fclose(fin);
    fclose(fout);
    printf("编码文件打印完成。\n");
}

//打印哈夫曼树
void printPretty(int idx, int depth, int isRight, FILE *fp) {
    if (idx == -1) return;

    printPretty(hfmTree[idx].rchild, depth + 1, 1, fp);

    for (int i = 0; i < depth; i++) {
        printf("    ");
        fprintf(fp, "    ");
    }

    if (depth > 0) {
        printf(isRight ? "┌── " : "└── ");
        fprintf(fp, isRight ? "┌── " : "└── ");
    }

    if (hfmTree[idx].lchild == -1)
        printf("%c(%u)\n", hfmTree[idx].ch, hfmTree[idx].weight),
        fprintf(fp, "%c(%u)\n", hfmTree[idx].ch, hfmTree[idx].weight);
    else
        printf("%u\n", hfmTree[idx].weight),
        fprintf(fp, "%u\n", hfmTree[idx].weight);

    printPretty(hfmTree[idx].lchild, depth + 1, 0, fp);
}
void printTree() {
    char treeFile[100], outFile[100];
    printf("输入哈夫曼树文件名：");
    scanf("%s", treeFile);
    printf("输入输出文件名：");
    scanf("%s", outFile);

    FILE *fp = fopen(treeFile, "rb");
    fread(&leafNum, sizeof(int), 1, fp);
    int totalNode = 2 * leafNum - 1;
    fread(hfmTree, sizeof(HuffmanNode), totalNode, fp);
    fclose(fp);

    FILE *out = fopen(outFile, "w");
    printPretty(totalNode - 1, 0, 0, out);
    fclose(out);
}

//主函数
int main() {
    char cmd;
    while (1) {
        printf("\nI-初始化 E-编码 D-译码 P-打印编码 T-打印树 Q-退出\n");
        printf("请输入指令：");
        scanf(" %c", &cmd);

        if (cmd == 'I') initHuffman();
        else if (cmd == 'E') encode();
        else if (cmd == 'D') decode();
        else if (cmd == 'P') printCode();
        else if (cmd == 'T') printTree();
        else if (cmd == 'Q') break;
        else printf("无效指令！\n");
    }
    return 0;
}
