/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-05-06 10:47:11
 * @Author       : Yygz314
 * @Date         : 2026-05-06 10:46:12
 * @blog         : https://yygz314.github.io/
 */
#include<bits/stdc++.h>
using namespace std;
class Solution {
public:
    vector<double> calcEquation(vector<vector<string>>& equations, vector<double>& values, vector<vector<string>>& queries) {
        int nvars = 0;
        unordered_map<string, int> variables;

        int n = equations.size();
        for (int i = 0; i < n; i++) {
            if (variables.find(equations[i][0]) == variables.end()) {
                variables[equations[i][0]] = nvars++;
            }
            if (variables.find(equations[i][1]) == variables.end()) {
                variables[equations[i][1]] = nvars++;
            }
        }

        vector<vector<pair<int, double>>> edges(nvars);
        for (int i = 0; i < n; i++) {
            int va = variables[equations[i][0]], vb = variables[equations[i][1]];
            edges[va].push_back(make_pair(vb, values[i]));
            edges[vb].push_back(make_pair(va, 1.0 / values[i]));
        }

        vector<double> ret;
        for (const auto& q: queries) {
            double result = -1.0;
            if (variables.find(q[0]) != variables.end() && variables.find(q[1]) != variables.end()) {
                int ia = variables[q[0]], ib = variables[q[1]];
                if (ia == ib) {
                    result = 1.0;
                } else {
                    queue<int> points;
                    points.push(ia);
                    vector<double> ratios(nvars, -1.0);
                    ratios[ia] = 1.0;

                    while (!points.empty() && ratios[ib] < 0) {
                        int x = points.front();
                        points.pop();

                        for (const auto [y, val]: edges[x]) {
                            if (ratios[y] < 0) {
                                ratios[y] = ratios[x] * val;
                                points.push(y);
                            }
                        }
                    }
                    result = ratios[ib];
                }
            }
            ret.push_back(result);
        }
        return ret;
    }
};

int main(){
    vector<vector<string>> equations = {{"a","b"},{"b","c"}};
    vector<double> values = {2.0,3.0};
    vector<vector<string>> queries = {{"a","c"},{"b","a"},{"a","e"},{"a","a"},{"x","x"}};
    vector<double> ans = Solution().calcEquation(equations, values, queries);
    for(auto i:ans){
        cout<<i<<" ";
    }
    return 0;
}

