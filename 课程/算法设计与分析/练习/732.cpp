/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-03-18 11:16:23
 * @Author       : Yygz314
 * @Date         : 2026-03-18 11:14:43
 * @blog         : https://yygz314.github.io/
 */
#include <iostream>
#include <unordered_map>
#include <algorithm>
using namespace std;

class MyCalendarThree {
private:
    // 动态开点线段树：key=节点编号，value={当前区间最大预定数, 懒标记}
    unordered_map<int, pair<int, int>> tree;
    const int MAX_RANGE = 1e9;

    void update(int start, int end, int l, int r, int idx, int val) {
        // 无交集，直接返回
        if (r < start || end < l) {
            return;
        }
        // 当前区间完全被包含：懒标记更新，不递归子节点
        if (start <= l && r <= end) {
            tree[idx].first += val;   // 更新当前区间最大预定数
            tree[idx].second += val; // 懒标记累加（延迟更新）
            return;
        }
        // 区间部分重叠：递归处理左右子树（位运算>>1 等价于/2，效率更高）
        int mid = (l + r) >> 1;
        update(start, end, l, mid, idx << 1, val);
        update(start, end, mid + 1, r, idx << 1 | 1, val);
        
        // 回溯更新：当前节点最大值 = 自身懒标记 + 子节点最大值（懒标记核心逻辑）
        tree[idx].first = tree[idx].second + max(tree[idx << 1].first, tree[idx << 1 | 1].first);
    }

public:
    // 构造函数：哈希表自动初始化，无需额外操作
    MyCalendarThree() = default;
    int book(int startTime, int endTime) {
        // 题目是左闭右开区间，转换为线段树闭区间 [startTime, endTime-1]
        update(startTime, endTime - 1, 0, MAX_RANGE, 1, 1);
        // 根节点存储全局最大重叠数，直接返回
        return tree[1].first;
    }
};

// 测试主函数
int main() {
    MyCalendarThree cal;
    cout << cal.book(10, 20) << endl;  
    cout << cal.book(50, 60) << endl;  
    cout << cal.book(10, 40) << endl;  
    cout << cal.book(5, 15) << endl;   
    cout << cal.book(5, 10) << endl;   
    cout << cal.book(25, 55) << endl;  
    return 0;
}