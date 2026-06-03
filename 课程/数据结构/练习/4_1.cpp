/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-10-13 14:26:03
 * @Author       : Yygz314
 * @Date         : 2025-10-13 13:24:06
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
void CreateList_L(LinkList &L,int n){
    //逆位输入n个元素
    // L =(LinkList)malloc(sizeof(LNode));
    // L->next = NULL; // 头指针指向空
    L = NULL; // 头指针指向空
    printf("请逆序输入%d个元素: ",n);
    for(int i = n;i>0;--i){
        LNode *p;
        p = (LinkList)malloc(sizeof(LNode));
        scanf("%d",&p->data);
        p->next = L;
        L = p;
    }
}
//将不带头节点的单链表所有节点原地逆转
void ReverseList_L(LinkList &L){
    LNode *p,*q;
    p = L;
    L = NULL;
    while(p){
        q = p->next;  // 暂存后继节点
        p->next = L;  // 将p链接在头结点之后
        L = p;  // 头结点指向新的头节点
        p = q;  // p后移
    }
}
// 打印单链表
void PrintList_L(LinkList L){
    LNode *p = L;
    while(p){
        printf("%d ",p->data);
        p = p->next;
    }
    printf("\n");
}

int main(){
    LinkList L1;
    CreateList_L(L1,5);
    PrintList_L(L1);
    ReverseList_L(L1);
    PrintList_L(L1);
    return 0;
}