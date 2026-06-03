/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-09-29 15:02:38
 * @Author       : Yygz314
 * @Date         : 2025-09-29 14:17:24
 * @blog         : https://www.cnblogs.com/Yygz314
 */
#include<iostream>
#include<cstdio>
using namespace std;
#define OK 1
#define ERROR 0
#define OVERFLOW -1
#define LIST_INIT_SIZE 100
#define LISTINCREMENT 10

typedef int Status;
typedef int ElemType;

// 单链表的存储结构
typedef struct LNode{
    ElemType data;
    struct LNode *next;
}LNode, *LinkList;

// 创建单链表
void CreateList_L(LinkList &L){ 
    // 正位输入n个元素
    L =(LinkList)malloc(sizeof(LNode));
    L->next = NULL; // 头指针指向空
    printf("逆序输入创建线性表。停止输入数据请输入'-1'\n");
    while(1){
        LNode *p;
        p = (LinkList)malloc(sizeof(LNode));
        scanf("%d",&p->data);
        if(p->data == -1){
            break;
        }
        p->next = L->next;
        L->next = p;
    }
}

// 打印单链表
void PrintList_L(LinkList L){
    LNode *p = L->next;
    while(p){
        printf("%d ",p->data);
        p = p->next;
    }
    printf("\n");
}

// 合并两个线性表
void MergerList_L(LinkList &La,LinkList &Lb,LinkList &Lc){
    LNode *pa,*pb,*pc;
    pa = La->next;
    pb = Lb->next;
    Lc = pc = La;
    while(pa && pb){
        pc->next = pa;
        pc = pa;
        pa = pa->next;
        pc->next = pb;
        pc = pb;
        pb = pb->next;
    }
    pc -> next = pa?pa:pb;
    free(Lb);
}


int main(){
    LinkList La,Lb,Lc;
    CreateList_L(La);
    CreateList_L(Lb);
    MergerList_L(La,Lb,Lc);
    printf("题2.23 合并后的线性表为: ");
    PrintList_L(Lc);

    return 0;
}