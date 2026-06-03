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
    printf("请逆序输入%d个元素: ",n);
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

// 清除单链表
void ClearList_L(LinkList &L){
    LNode *p;
    while(L->next){
        p = L->next;
        L->next = p->next;
        free(p);
    }
}

// 获取第i个元素
Status GetElem_L(LinkList L,int i,ElemType &e){
    LinkList p;
    int j;
    p = L->next;j=1;
    while(p && j<i){
        p = p->next;;
        ++j;
    }
    if(!p||j>i) return ERROR;
    e = p->data;
    return OK;
}

//指定位置插入
Status ListInsert_L(LinkList &L,int i,ElemType e){
    LinkList p;int j;
    p = L;j = 0;
    while(p && j<i-1){
        p = p->next;
        ++j;
    }
    if(!p || j>i-1) return ERROR;
    LNode *s;
    s = (LinkList)malloc(sizeof(LNode));
    s->data = e;
    s->next = p->next;
    p->next = s;
    return OK;
}

// 删除指定位置的元素
Status ListDelete_L(LinkList &L,int i,ElemType &e){
    LinkList p;int j;
    p = L;j = 0;
    while(p && j<i-1){
        p = p->next;
        ++j;
    }
    if(!p || j>i-1) return ERROR;
    LinkList q = p->next;
    p->next = q->next;
    e = q->data;
    free(q);
    return OK;
}

// 获取链表长度
int GetLength_L(LinkList L){
    int len = 0;
    LinkList p = L->next;
    while(p){
        ++len;
        p = p->next;
    }
    return len;
}

// 合并两个线性表
void MergerList_L(LinkList &La,LinkList &Lb,LinkList &Lc){
    LNode *pa,*pb,*pc;
    pa = La->next;
    pb = Lb->next;
    Lc = pc = La;
    while(pa && pb){
        if(pa->data <= pb->data){
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
    LinkList L1;
    CreateList_L(L1,5);
    PrintList_L(L1);
    ElemType e1;
    GetElem_L(L1,3,e1);
    printf("第3个元素为:%d\n",e1);
    ListInsert_L(L1,2,10);   
    printf("插入元素后单链表L1:");
    PrintList_L(L1);
    ElemType e2;
    ListDelete_L(L1,2,e2);
    printf("删除的元素为:%d\n",e2);
    printf("删除元素后单链表L1:");
    PrintList_L(L1);
    int len = GetLength_L(L1);
    printf("单链表L的长度为:%d\n",len);
    LinkList L2,L;
    CreateList_L(L2,5);
    MergerList_L(L1,L2,L);
    printf("合并后的单链表L:");
    PrintList_L(L);

    ClearList_L(L1);
    printf("清除单链表L后:");
    PrintList_L(L);
    return 0;
}