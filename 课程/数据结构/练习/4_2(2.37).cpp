/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-10-13 15:56:01
 * @Author       : Yygz314
 * @Date         : 2025-10-13 14:26:51
 * @blog         : https://www.cnblogs.com/Yygz314
 */
#include<iostream>
#include<cstdio>
#include<cstdlib>
using namespace std;

#define OK 1
#define ERROR 0
#define OVERFLOW -1

typedef int Status;
typedef int ElemType;

// 双向循环链表节点结构
typedef struct DuLNode {
    ElemType data;
    struct DuLNode *prior, *next;
} DuLNode, *DuLinkList;

// 创建带头结点的双向循环链表（正序输入）
Status CreateList_DuL(DuLinkList &L, int n) {
    L = (DuLinkList)malloc(sizeof(DuLNode));
    if (!L) return OVERFLOW; 
    L->next = L;
    L->prior = L; 
    DuLinkList tail = L; 
    printf("请正序输入%d个元素: ", n);
    for (int i = 0; i < n; i++) {
        DuLinkList s = (DuLinkList)malloc(sizeof(DuLNode));
        if (!s) return OVERFLOW;
        scanf("%d", &s->data);
        s->prior = tail;
        s->next = L;
        tail->next = s;
        L->prior = s;
        tail = s; 
    }
    return OK;
}

// 改造双向循环链表
Status TransformList_DuL(DuLinkList &L) {
    if (L->next == L) { 
        printf("空链表无法改造\n");
        return ERROR;
    }

    DuLinkList p = L->next; 
    DuLinkList even_head = NULL; 
    DuLinkList even_tail = NULL; 

    while (p->next != L) { 
        DuLinkList q = p->next; 
        DuLinkList next_q = q->next; 

        // 将q从原链表中拆出
        p->next = next_q; 
        next_q->prior = p; 

        // 将q添加到偶数链的头部
        if (even_head == NULL) {
            even_head = q;
            even_tail = q;
            q->next = q; 
            q->prior = q;
        } else {
            q->next = even_head; 
            q->prior = even_head->prior;
            even_head->prior->next = q; 
            even_head->prior = q; 
            even_head = q;
        }

        // 移动p到下一个奇数节点
        if (next_q != L) {
            p = next_q;
        }
    }

    // 将偶数链连接到奇数链的末尾
    if (even_head != NULL) {
        p->next = even_head;
        even_head->prior = p;
        even_tail->next = L;
        L->prior = even_tail;
    }
    return OK;
}

// 打印
Status PrintList_DuL(DuLinkList L) {
    if (L->next == L) { 
        printf("链表为空\n");
        return ERROR;
    }
    DuLinkList p = L->next;
    while (p != L) {
        printf("%d ", p->data);
        p = p->next;
    }
    printf("\n");
    return OK;
}

int main() {
    DuLinkList L;
    int n;
    printf("请输入链表长度n: ");
    scanf("%d", &n);
    CreateList_DuL(L, n);
    printf("原链表: ");
    PrintList_DuL(L);
    TransformList_DuL(L);
    printf("改造后链表: ");
    PrintList_DuL(L);
    return OK;
}