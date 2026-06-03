#include <stdio.h>
#include <stdlib.h>
#include <limits.h> 

#define MAX_VERTEX_NUM 20  
#define INFINITY INT_MAX   
#define TRUE 1
#define FALSE 0
#define OK 1
#define ERROR 0
typedef int Status;
typedef int Boolean;
typedef char VertexType;  // 顶点类型
typedef int VRType;       // 弧的权值类型
typedef int InfoType;     // 弧的附加信息类型

typedef enum { DG, DN, UDG, UDN } GraphKind;  

// 弧的结构体
typedef struct ArcCell {
    VRType adj;        // 权值
    InfoType *info;    // 弧的附加信息
} ArcCell, AdjMatrix[MAX_VERTEX_NUM][MAX_VERTEX_NUM];

// 图的结构体
typedef struct {
    VertexType vexs[MAX_VERTEX_NUM];  
    AdjMatrix arcs;                   
    int vexnum, arcnum;              
    GraphKind kind;                   
} Graph;

// 队列结构体
typedef struct {
    int data[MAX_VERTEX_NUM];
    int front, rear;  
} Queue;
Boolean visited[MAX_VERTEX_NUM];
Status (*VisitFunc)(int v, VertexType *vexs);


int LocateVex(Graph G, VertexType v) {
    for (int i = 0; i < G.vexnum; i++) {
        if (G.vexs[i] == v) {
            return i;  
        }
    }
    return -1;  
}

// 2. 创建无向图
Status CreateUDN(Graph *G) {
    int IncInfo;  
    printf("请输入：顶点数 弧数 是否含弧信息（0/1）：");
    scanf("%d%d%d", &G->vexnum, &G->arcnum, &IncInfo);

    // 输入顶点信息
    printf("请输入%d个顶点（以空格分隔）：", G->vexnum);
    for (int i = 0; i < G->vexnum; i++) {
        scanf(" %c", &G->vexs[i]);  
    }

    // 初始化邻接矩阵（默认“无边”）
    for (int i = 0; i < G->vexnum; i++) {
        for (int j = 0; j < G->vexnum; j++) {
            G->arcs[i][j].adj = INFINITY;
            G->arcs[i][j].info = NULL;
        }
    }

    // 输入弧的信息（顶点对 + 权值）
    printf("请输入%d条弧（格式：顶点1 顶点2 权值）：\n", G->arcnum);
    for (int k = 0; k < G->arcnum; k++) {
        VertexType v1, v2;
        VRType w;
        scanf(" %c %c %d", &v1, &v2, &w);

        // 定位顶点索引
        int i = LocateVex(*G, v1);
        int j = LocateVex(*G, v2);
        if (i == -1 || j == -1) {
            printf("顶点不存在！创建失败\n");
            return ERROR;
        }

        // 无向网：邻接矩阵对称
        G->arcs[i][j].adj = w;
        G->arcs[j][i].adj = w;

        // （可选）弧的附加信息（这里简化为不处理）
        if (IncInfo) {
            G->arcs[i][j].info = NULL;
            G->arcs[j][i].info = NULL;
        }
    }
    return OK;
}


Status CreateGraph(Graph *G) {
    printf("请输入图的类型（0:DG有向图 1:DN有向网 2:UDG无向图 3:UDN无向网）：");
    int kind;
    scanf("%d", &kind);
    G->kind = (GraphKind)kind;

    switch (G->kind) {
        case UDN:
            return CreateUDN(G);  // 以无向网为例
        default:
            printf("暂未实现该类型图的创建！\n");
            return ERROR;
    }
}

Status Visit(int v, VertexType *vexs) {
    printf("%c ", vexs[v]);
    return OK;
}

//DFS
void DFS(Graph G, int v) {
    visited[v] = TRUE;
    VisitFunc(v, G.vexs);  
    for (int w = 0; w < G.vexnum; w++) {
        if (G.arcs[v][w].adj != INFINITY && !visited[w]) {
            DFS(G, w);
        }
    }
}

void DFSTraverse(Graph G) {
    VisitFunc = Visit;  
    for (int v = 0; v < G.vexnum; v++) {
        visited[v] = FALSE;
    }
    printf("深度优先遍历序列：");
    for (int v = 0; v < G.vexnum; v++) {
        if (!visited[v]) { 
            DFS(G, v);
        }
    }
    printf("\n");
}


Status InitQueue(Queue *Q) {
    Q->front = Q->rear = 0;
    return OK;
}

Status EnQueue(Queue *Q, int e) {
    if ((Q->rear + 1) % MAX_VERTEX_NUM == Q->front) {
        return ERROR;  
    }
    Q->data[Q->rear] = e;
    Q->rear = (Q->rear + 1) % MAX_VERTEX_NUM;
    return OK;
}

Status DeQueue(Queue *Q, int *e) {
    if (Q->front == Q->rear) {
        return ERROR;  
    }
    *e = Q->data[Q->front];
    Q->front = (Q->front + 1) % MAX_VERTEX_NUM;
    return OK;
}
Boolean QueueEmpty(Queue Q) {
    return Q.front == Q.rear;
}


//BFS
void BFSTraverse(Graph G) {
    Queue Q;
    for (int v = 0; v < G.vexnum; v++) {
        visited[v] = FALSE;
    }
    InitQueue(&Q);
    printf("广度优先遍历序列：");
    for (int v = 0; v < G.vexnum; v++) {
        if (!visited[v]) {  
            visited[v] = TRUE;
            VisitFunc(v, G.vexs);  
            EnQueue(&Q, v);       
            while (!QueueEmpty(Q)) {
                int u;
                DeQueue(&Q, &u); 
                for (int w = 0; w < G.vexnum; w++) {
                    if (G.arcs[u][w].adj != INFINITY && !visited[w]) {
                        visited[w] = TRUE;
                        VisitFunc(w, G.vexs);
                        EnQueue(&Q, w);
                    }
                }
            }
        }
    }
    printf("\n");
}

int main() {
    Graph G;
    if (CreateGraph(&G) != OK) {
        return 1;
    }
    DFSTraverse(G);
    BFSTraverse(G);
    return 0;
}