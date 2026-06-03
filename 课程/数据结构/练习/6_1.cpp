/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-10-27 13:52:57
 * @Author       : Yygz314
 * @Date         : 2025-10-27 13:16:52
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
typedef int QElemType;

// 队列链式表示
typedef struct QNode{
    QElemType data;
    struct QNode *next;
}QNode,*QueuePtr;
typedef struct{
    QueuePtr front;
    QueuePtr rear;
}LinkQueue;

// 初始化队列
Status InitQueue(LinkQueue &Q){
    Q.front = Q.rear = (QueuePtr)malloc(sizeof(QNode));
    if(!Q.front) exit(OVERFLOW);
    Q.front->next=NULL;
    return OK;
}

// 销毁队列
Status DestroyQueue(LinkQueue &Q){
    while(Q.front){
        Q.rear = Q.front->next;
        free(Q.front);
        Q.front = Q.rear;
    }
    return OK;
}

// 入队
Status EnQueue(LinkQueue &Q, QElemType e){
    QueuePtr p = (QueuePtr) malloc (sizeof(QNode));
    if(!p) exit(OVERFLOW);
    if(!p) exit(OVERFLOW);
    p->data = e;
    p->next = NULL;
    Q.rear->next = p;
    Q.rear = p;
    return OK;
}

// 出队
Status DeQueue(LinkQueue &Q,QElemType &e){
    if(Q.front==Q.rear) return ERROR;
    QueuePtr p = Q.front->next;
    e = p->data;
    Q.front->next = p->next;
    if(Q.rear == p) Q.rear = Q.front;
    free(p);
    return OK;
}

//打印链表
void PrintQueue(LinkQueue Q){
    QueuePtr p = Q.front->next;
    while(p){
        cout<<p->data<<" ";
        p = p->next;
    }
    cout<<endl;
}

int main(){
    LinkQueue Q;
    InitQueue(Q);
    cout<<"初始化队列：";
    PrintQueue(Q);
    EnQueue(Q,1);
    EnQueue(Q,2);
    EnQueue(Q,3);
    EnQueue(Q,4);
    EnQueue(Q,5);
    cout<<"入队后队列为：";
    PrintQueue(Q);
    QElemType e;
    DeQueue(Q,e);
    cout<<"出队元素为："<<e<<endl;
    DeQueue(Q,e);
    cout<<"出队元素为："<<e<<endl;
    cout<<"出队后队列为：";
    PrintQueue(Q);
    DestroyQueue(Q);
    cout<<"销毁后队列:";
    PrintQueue(Q);
    return 0;
}
