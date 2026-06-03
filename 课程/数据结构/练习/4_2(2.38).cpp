#include<iostream>
#include<cstdio>
#include<cstdlib>
using namespace std;

#define OK 1
#define ERROR 0
#define OVERFLOW -1

typedef int Status;
typedef int ElemType;

// 双向循环链表
typedef struct DuLNode {
    ElemType data;
    struct DuLNode *prior, *next;
    int freq; 
} DuLNode, *DuLinkList;

// 创建链表
Status CreateList_DuL(DuLinkList &L, int n) {
    L = (DuLinkList)malloc(sizeof(DuLNode));
    if (!L) return OVERFLOW; 
    L->next = L; 
    L->prior = L; 

    DuLinkList tail = L; // tail指向当前链表末尾（初始为头节点）
    printf("请正序输入%d个元素（用空格分隔）: ", n);
    for (int i = 0; i < n; i++) {
        DuLinkList s = (DuLinkList)malloc(sizeof(DuLNode));
        if (!s) return OVERFLOW;
        scanf("%d", &s->data);
        s->freq = 0; 
        s->next = L;         
        s->prior = tail;     
        tail->next = s;      
        L->prior = s;        
        tail = s;          
    }
    return OK;
}

// LOCATE操作
Status Locate_DuL(DuLinkList &L, ElemType x) {
    if (L->next == L) { 
        printf("空链表无法执行LOCATE操作\n");
        return ERROR;
    }

    DuLinkList target = NULL;
    DuLinkList p = L->next;
    while (p != L) {
        if (p->data == x) {
            target = p;
            break;
        }
        p = p->next;
    }

    if (target == NULL) {
        printf("未找到元素%d\n", x);
        return ERROR;
    }

    target->freq++;

    if (L->next == L->prior) {
        return OK;
    }

    // 删除target
    DuLinkList prev_old = target->prior; 
    DuLinkList next_old = target->next; 
    prev_old->next = next_old;           
    next_old->prior = prev_old;         
    // 找插入位置
    DuLinkList prev_insert = prev_old;
    while (prev_insert != L && prev_insert->freq < target->freq) {
        prev_insert = prev_insert->prior; 
    }
    // 插入
    target->next = prev_insert->next;      
    target->prior = prev_insert;         
    prev_insert->next->prior = target;    
    prev_insert->next = target;         
    return OK;
}

// 打印
Status PrintList_DuL(DuLinkList L) {
    if (L->next == L) {
        printf("链表为空\n");
        return ERROR;
    }
    printf("链表顺序（含频度）: ");
    DuLinkList p = L->next;
    while (p != L) {
        printf("%d(freq=%d) ", p->data, p->freq); // 打印数据和频度，方便调试
        p = p->next;
    }
    printf("\n");
    return OK;
}

int main() {
    DuLinkList L;
    int n, x;
    printf("请输入链表长度n: ");
    scanf("%d", &n);
    if (CreateList_DuL(L, n) != OK) {
        printf("链表创建失败\n");
        return ERROR;
    }
    PrintList_DuL(L);
    // 2. 多次测试LOCATE操作
    do {
        printf("\n请输入要LOCATE的元素x（输入-1退出）: ");
        scanf("%d", &x);
        if (x == -1) break;
        if (Locate_DuL(L, x) == OK) {
            printf("LOCATE后：\n");
            PrintList_DuL(L);
        } else {
            printf("LOCATE失败\n");
        }
    } while (true);
    return OK;
}