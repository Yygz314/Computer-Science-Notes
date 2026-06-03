#include<iostream>
using namespace std;
#define OK 1
#define ERROR 0
#define OVERFLOW -1
#define LIST_INIT_SIZE 100
#define LISTINCREMENT 10

typedef int Status;
typedef int ElemType;
// 定义顺序表
typedef struct{
    ElemType *elem; //储存空间
    int length;     //表长
    int listsize;   //存储容量
}SqList;

//创建空顺序表
Status InitList_sq(SqList &L){
    L.elem = (ElemType *)malloc(LIST_INIT_SIZE*sizeof(ElemType));
    if(!L.elem) exit(OVERFLOW);
    L.length = 0;
    L.listsize = LIST_INIT_SIZE;
    return OK;
}

//指定位置插入元素
Status ListInsert_Sq(SqList &L,int i,ElemType e){
    if(i<1 || i>L.length+1) return ERROR;
    if(L.length >= L.listsize){
        ElemType *newbase = (ElemType *)realloc(L.elem,(L.listsize+LISTINCREMENT)*sizeof(ElemType));
        if(!newbase) exit(OVERFLOW);
        L.elem = newbase;
        L.listsize += LISTINCREMENT;
    }
    ElemType *q = &(L.elem[i-1]);   //q为插入位置
    for(ElemType *p = &(L.elem[L.length-1]);p>=q;--p)
        *(p+1) = *p;
    *q = e;
    ++L.length;
    return OK;
}

//指定位置删除元素并返回被删除元素
Status ListDelete_Sq(SqList &L,int i,ElemType &e){
    if(i<1 || i>L.length) return ERROR;
    ElemType *p = &(L.elem[i-1]);   //p为删除位置
    e = *p;
    ElemType *q = L.elem + L.length -1;   
    for(++p;p<=q;++p)
        *(p-1) = *p;
    --L.length;
    return OK;
}

//销毁顺序表
Status DestroyList_sq(SqList &L){
    free(L.elem);
    L.elem = NULL;
    L.length = 0;
    L.listsize = 0;
    return OK;
}

//打印顺序表
void PrintList_Sq(SqList L){
    for(int i = 0;i<L.length;++i){
        printf("%d ",L.elem[i]);
    }
    printf("\n");
}

//获取顺序表最大值
ElemType Get_Max(SqList L){
    if(L.length == 0) return ERROR;
    ElemType max = L.elem[0];
    for(int i = 1;i<L.length;i++){
        if(L.elem[i]>max){
            max = L.elem[i];
        }
    }
    return max;
}

//获取顺序表最小值
ElemType Get_Min(SqList L){
    if(L.length == 0) return ERROR;
    ElemType min = L.elem[0];
    for(int i = 1;i<L.length;i++){
        if(L.elem[i]<min){
            min = L.elem[i];
        }
    }
    return min;
}

//删除顺序表中所有大于min且小于max的元素
Status Deleterange_Sq(SqList &L,ElemType min,ElemType max){
    int i = 0;
    while(i<L.length){
        if(L.elem[i]>min && L.elem[i]<max){
            ListDelete_Sq(L,i,L.elem[i]);
        }else ++i;
    }
    return OK;
}



int main(){
    SqList L;
    InitList_sq(L);
    printf("初始化顺序表L:");
    PrintList_Sq(L);
    ListInsert_Sq(L,1,9);
    ListInsert_Sq(L,2,19);
    ListInsert_Sq(L,3,3);
    ListInsert_Sq(L,4,7);
    printf("插入元素后顺序表L:");
    PrintList_Sq(L);
    ElemType e;
    ListDelete_Sq(L,2,e);
    printf("删除的元素为:%d\n",e);
    printf("删除元素后顺序表L:");
    PrintList_Sq(L);
    ElemType max = Get_Max(L);
    ElemType min = Get_Min(L);
    printf("顺序表L的最大值为:%d\n",max);
    printf("顺序表L的最小值为:%d\n",min);
    Deleterange_Sq(L,min,max);
    printf("删除所有大于min且小于max的元素后顺序表L:");
    PrintList_Sq(L);
    DestroyList_sq(L);
    printf("顺序表L已被销毁\n");
    return 0;
}
