#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
    unsigned int weight;
    unsigned int parent, lchild, rchild;
} HTNode, *HuffmanTree;
typedef char **HuffmanCode;

void Select(HuffmanTree HT, int len, int *s1, int *s2) {
    unsigned int min1, min2;
    int i;
    for (i = 1; i <= len; i++) {
        if (HT[i].parent == 0) {
            *s1 = i;
            min1 = HT[i].weight;
            break;
        }
    }
    for (; i <= len; i++) {
        if (HT[i].parent == 0 && HT[i].weight < min1) {
            min1 = HT[i].weight;
            *s1 = i;
        }
    }
    for (i = 1; i <= len; i++) {
        if (HT[i].parent == 0 && i != *s1) {
            *s2 = i;
            min2 = HT[i].weight;
            break;
        }
    }
    for (; i <= len; i++) {
        if (HT[i].parent == 0 && i != *s1 && HT[i].weight < min2) {
            min2 = HT[i].weight;
            *s2 = i;
        }
    }
}

void HuffmanCoding(HuffmanTree *HT, HuffmanCode *HC, int *w, int n) {
    int m, i, s1, s2;
    HuffmanTree p;
    char *cd;
    int start, c, f;
    if (n <= 1) return;
    m = 2 * n - 1; 
    *HT = (HuffmanTree)malloc((m + 1) * sizeof(HTNode)); 

    for (p = *HT + 1, i = 1; i <= n; ++i, ++p, ++w) {
        p->weight = *w;
        p->parent = 0;
        p->lchild = 0;
        p->rchild = 0;
    }
    for (; i <= m; ++i, ++p) {
        p->weight = 0;
        p->parent = 0;
        p->lchild = 0;
        p->rchild = 0;
    }

    // 构建赫夫曼树
    for (i = n + 1; i <= m; ++i) {
        Select(*HT, i - 1, &s1, &s2);
        (*HT)[s1].parent = i;
        (*HT)[s2].parent = i;
        (*HT)[i].lchild = s1;
        (*HT)[i].rchild = s2;
        (*HT)[i].weight = (*HT)[s1].weight + (*HT)[s2].weight;
    }

    *HC = (HuffmanCode)malloc((n + 1) * sizeof(char *));
    cd = (char *)malloc(n * sizeof(char)); 
    cd[n - 1] = '\0'; 
    for (i = 1; i <= n; ++i) {
        start = n - 1;
        for (c = i, f = (*HT)[i].parent; f != 0; c = f, f = (*HT)[f].parent) {
            if ((*HT)[f].lchild == c) {
                cd[--start] = '0';
            } else {
                cd[--start] = '1';
            }
        }
        (*HC)[i] = (char *)malloc((n - start) * sizeof(char));
        strcpy((*HC)[i], &cd[start]);
    }
    free(cd); 
}


int main() {
    int n, i;
    int *w;
    HuffmanTree HT;
    HuffmanCode HC;
    printf("个数：");
    scanf("%d", &n);
    w = (int *)malloc(n * sizeof(int));
    printf("权值（整数）：\n");
    for (i = 0; i < n; ++i) {
        scanf("%d", &w[i]);
    }

    HuffmanCoding(&HT, &HC, w, n);
    printf("赫夫曼编码结果：\n");
    for (i = 1; i <= n; ++i) {
        printf("权值%d,编码：%s\n", HT[i].weight, HC[i]);
    }

    // 释放内存
    for (i = 1; i <= n; ++i) {
        free(HC[i]);
    }
    free(HC);
    free(HT);
    free(w);
    return 0;
}