#include<iostream>
#include<stack>
using namespace std;

#define OK 1
#define ERROR 0
#define OVERFLOW -1
#define MAX_ARRAY_DIM 8
#define MAX_TREE_SIZE 100

typedef int Status;
typedef char TElemType;

// 线索二叉树节点结构
typedef enum PointerTag { Link, Thread } PointerTag; 
typedef struct BiThrNode {
    TElemType data;
    struct BiThrNode *lchild, *rchild;
    PointerTag LTag, RTag; 
} BiThrNode, *BiThrTree; 

BiThrTree pre;

// 二叉树的创建
Status CreateBiTree(BiThrTree &T) {
    TElemType ch;
    cin >> ch;
    if (ch == '#') T = NULL; 
    else {
        T = new BiThrNode; 
        T->data = ch;
        T->LTag = Link; 
        T->RTag = Link; 
        CreateBiTree(T->lchild); 
        CreateBiTree(T->rchild); 
    }
    return OK;
}


Status Visit(TElemType ch) {
    cout << ch << " ";
    return OK;
}

// 中序递归遍历
Status InOrderTraverse(BiThrTree T, Status (*visit)(TElemType)) {
    if (T) {
        InOrderTraverse(T->lchild, visit); 
        visit(T->data);                   
        InOrderTraverse(T->rchild, visit); 
        return OK;
    }
    return OK;
}

// 中序非递归遍历
Status InOrderTraverses(BiThrTree T, Status (*visit)(TElemType)) {
    stack<BiThrTree> S;
    BiThrTree p = T;
    while (p != NULL || !S.empty()) {
        if (p != NULL) {
            S.push(p);
            p = p->lchild;
        } else {
            p = S.top();
            S.pop();
            visit(p->data); 
            p = p->rchild; 
        }
    }
    return OK;
}

// 中序线索化
void InThreading(BiThrTree p) {
    if (p) {
        InThreading(p->lchild); 
        if (!p->lchild) {
            p->LTag = Thread; 
            p->lchild = pre;  
        }
        if (!pre->rchild) {
            pre->RTag = Thread; 
            pre->rchild = p;   
        }
        pre = p; 
        InThreading(p->rchild); 
    }
}

// 构建中序线索树
Status InOrderThreading(BiThrTree &Thrt, BiThrTree T) {
    if (!(Thrt = new BiThrNode)) exit(OVERFLOW);
    Thrt->LTag = Link;    
    Thrt->RTag = Thread;  
    Thrt->rchild = Thrt;  
    if (!T) { 
        Thrt->lchild = Thrt; 
    } else {
        Thrt->lchild = T; 
        pre = Thrt;       
        InThreading(T); 
        pre->rchild = Thrt;
        pre->RTag = Thread;
        Thrt->rchild = pre; 
    }
    return OK;
}

// 中序线索化遍历
Status InorderTraverse_Thr(BiThrTree T, Status (*Visit)(TElemType e)) {
    BiThrTree p = T->lchild; 
    while (p != T) { 
        while (p->LTag == Link) {
            p = p->lchild;
        }
        if (!Visit(p->data)) return ERROR;
        while (p->RTag == Thread && p->rchild != T) {
            p = p->rchild;
            Visit(p->data); 
        }
        p = p->rchild;
    }
    return OK;
}


int main() {
    BiThrTree T; 
    cout << "请按前序遍历输入二叉树（#表示空节点）：" << endl;
    CreateBiTree(T);
    cout << "中序递归遍历结果：";
    InOrderTraverse(T, Visit);
    cout << endl;
    cout << "中序非递归遍历结果：";
    InOrderTraverses(T, Visit);
    cout << endl;

    BiThrTree Thrt; 
    InOrderThreading(Thrt, T); 
    cout << "中序线索化遍历结果：";
    InorderTraverse_Thr(Thrt, Visit); 
    cout << endl;

    return 0;
}