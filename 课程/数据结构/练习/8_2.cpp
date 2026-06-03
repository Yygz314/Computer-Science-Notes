#include<stdarg.h>
#include<iostream>
#include<cstdio>
#include<cstdlib>
#include<cstring>  // 用于memset初始化数组
using namespace std;

#define OK 1
#define ERROR 0
#define OVERFLOW -1
#define MAX_ARRAY_DIM 8
#define UNDERFLOW -2

typedef int Status;
typedef int ElemType;

typedef struct{
    ElemType *base;       
    int dim;              
    int *bounds;          
    int *constants;       
}Array;


// 初始化数组
Status InitArray(Array &A, int dim, ...){
    va_list ap;
    if(dim < 1 || dim > MAX_ARRAY_DIM) return ERROR;
    A.dim = dim;

    A.bounds = (int *)malloc(dim * sizeof(int));
    if(!A.bounds) exit(OVERFLOW);
    int elemtotal = 1;  
    va_start(ap, dim);  
    for(int i = 0; i < dim; ++i){
        A.bounds[i] = va_arg(ap, int);
        if(A.bounds[i] < 1) return UNDERFLOW;  
        elemtotal *= A.bounds[i];
    }
    va_end(ap);
    
    A.base = (ElemType *)malloc(elemtotal * sizeof(ElemType));
    if(!A.base) exit(OVERFLOW); 
    memset(A.base, 0, elemtotal * sizeof(ElemType));  

    A.constants = (int *)malloc(dim * sizeof(int));
    if(!A.constants) exit(OVERFLOW);
    
    A.constants[dim - 1] = 1;  
    for(int i = dim - 2; i >= 0; --i){
        A.constants[i] = A.bounds[i + 1] * A.constants[i + 1];
    }
    return OK;
}

// 销毁数组（释放所有动态内存）
Status DestroyArray(Array &A){  
    if(A.base) free(A.base);   
    A.base = NULL;
    if(A.bounds) free(A.bounds); 
    A.bounds = NULL;
    if(A.constants) free(A.constants); 
    A.constants = NULL;
    A.dim = 0;  
    return OK;
}

// 计算元素在数组中的偏移量（内部辅助函数）
Status Locate(Array A, va_list ap, int &off){ 
    off = 0;  
    int ind;  
    for(int i = 0; i < A.dim; ++i){    
        ind = va_arg(ap, int); 
        if(ind < 0 || ind >= A.bounds[i]) return OVERFLOW;  
        off += A.constants[i] * ind; 
    }
    return OK;
}

// 获取数组指定位置的元素值
Status Value(Array A, ElemType &e, ...){
    if(!A.base) return ERROR; 
    va_list ap; 
    va_start(ap, e);
    int off;
    Status result = Locate(A, ap, off);
    if(result <= 0) return result;
    e = *(A.base + off);  
    va_end(ap);  
    return OK;
}

// 给数组指定位置赋值
Status Assign(Array *A, ElemType e, ...){
    if(!A || !A->base) return ERROR;  
    va_list ap;
    va_start(ap, e);
    int off;
    Status result = Locate(*A, ap, off);  
    if(result <= 0) return result;
    *(A->base + off) = e;  
    va_end(ap);  
    return OK;
}

Status PrintArray(Array A){
    if(!A.base || !A.bounds || !A.constants || A.dim < 1 || A.dim > MAX_ARRAY_DIM){
        cout << "数组不合法，无法打印！" << endl;
        return ERROR;
    }

    int dim = A.dim;
    int *index = (int *)malloc(dim * sizeof(int));  
    if(!index) exit(OVERFLOW);
    memset(index, 0, dim * sizeof(int));  

    cout << "多维数组（" << dim << "维）内容：" << endl;
    bool isFirstElem = true;  

    while(true){
        int off = 0;
        for(int i = 0; i < dim; ++i){
            off += index[i] * A.constants[i];
        }
        if(!isFirstElem) cout << " ";
        cout << *(A.base + off);
        isFirstElem = false;
        int k = dim - 1;  
        while(k >= 0 && index[k] == A.bounds[k] - 1){
            index[k] = 0;  
            k--;          
        }

        if(k < 0) break;

        index[k]++;

        if(k < dim - 1){
            cout << endl;
            isFirstElem = true;
        }
    }

    free(index);
    cout << endl << endl;  
    return OK;
}

// 主函数测试
int main(){
    Array A;
    if(InitArray(A, 2, 5, 4) == OK){
        PrintArray(A);  
        
        Assign(&A, 10, 1, 2);   
        Assign(&A, 20, 3, 0);   
        PrintArray(A);         
        
        ElemType e1, e2;
        Value(A, e1, 1, 2);     
        Value(A, e2, 3, 0);     
        cout << "A[1][2] = " << e1 << endl;  
        cout << "A[3][0] = " << e2 << endl;  
        
        DestroyArray(A);        
    }
    return 0;
}