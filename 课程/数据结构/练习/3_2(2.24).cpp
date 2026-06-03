/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-09-29 14:50:43
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
void CreateList_L(LinkList &L,int n){
    //逆位输入n个元素
    L =(LinkList)malloc(sizeof(LNode));
    L->next = NULL; // 头指针指向空
    printf("请输入%d个元素: ",n);
    for(int i = n;i>0;--i){
        LNode *p;
        p = (LinkList)malloc(sizeof(LNode));
        scanf("%d",&p->data);
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

int GetLength_L(LinkList L){
    int len = 0;
    LinkList p = L->next;
    while(p){
        ++len;
        p = p->next;
    }
    return len;
}

void MergerList_L(LinkList &La,LinkList &Lb,LinkList &Lc){
    LNode *pa,*pb,*pc;
    pa = La->next;
    pb = Lb->next;
    Lc = pc = La;
    while(pa && pb){
        if(pa->data >= pb->data){
            pc->next = pa;
            pc = pa;
            pa = pa->next;
        }else{
            pc->next = pb;
            pc = pb;
            pb = pb->next;
        }
    }
    pc -> next = pa?pa:pb;
    free(Lb);
}

int main(){
    LinkList La,Lb,Lc;
    int n,m;
    printf("请输入第一个线性表的长度: ");
    scanf("%d",&n);
    CreateList_L(La,n);
    printf("请输入第二个线性表的长度: ");
    scanf("%d",&m);
    CreateList_L(Lb,m);
    MergerList_L(La,Lb,Lc);
    printf("题2.24 合并后的线性表为: ");
    PrintList_L(Lc);

    return 0;
}