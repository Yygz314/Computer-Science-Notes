#include<iostream>
#include<vector>
#include<map>
#include<algorithm>
using namespace std;

class UnionFind {
public:
    vector<int> parent;

    UnionFind(int n) {
        parent.resize(n);
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    // 修复：union是关键字，重命名为unite
    void unite(int index1, int index2) {
        parent[find(index2)] = find(index1);
    }

    int find(int index) {
        if (parent[index] != index) {
            parent[index] = find(parent[index]);
        }
        return parent[index];
    }
};

class Solution {
public:
    vector<vector<string>> accountsMerge(vector<vector<string>>& accounts) {
        map<string, int> emailToIndex;
        map<string, string> emailToName;
        int emailsCount = 0;
        for (auto& account : accounts) {
            string& name = account[0];
            int size = account.size();
            for (int i = 1; i < size; i++) {
                string& email = account[i];
                if (!emailToIndex.count(email)) {
                    emailToIndex[email] = emailsCount++;
                    emailToName[email] = name;
                }
            }
        }
        UnionFind uf(emailsCount);
        for (auto& account : accounts) {
            string& firstEmail = account[1];
            int firstIndex = emailToIndex[firstEmail];
            int size = account.size();
            for (int i = 2; i < size; i++) {
                string& nextEmail = account[i];
                int nextIndex = emailToIndex[nextEmail];
                uf.unite(firstIndex, nextIndex); // 对应修改函数名
            }
        }
        map<int, vector<string>> indexToEmails;
        for (auto& [email, _] : emailToIndex) {
            int index = uf.find(emailToIndex[email]);
            vector<string>& account = indexToEmails[index];
            account.emplace_back(email);
            indexToEmails[index] = account;
        }
        vector<vector<string>> merged;
        for (auto& [_, emails] : indexToEmails) {
            sort(emails.begin(), emails.end());
            string& name = emailToName[emails[0]];
            vector<string> account;
            account.emplace_back(name);
            for (auto& email : emails) {
                account.emplace_back(email);
            }
            merged.emplace_back(account);
        }
        return merged;
    }
};

// 修复：遍历输出二维vector，不能直接cout
void printResult(const vector<vector<string>>& res) {
    for (auto& account : res) {
        for (auto& s : account) {
            cout << s << " ";
        }
        cout << endl;
    }
}

int main() {
    vector<vector<string>> accounts = {
        {"John","johnsmith@mail.com","john00@mail.com"},
        {"John","johnnybravo@mail.com"},
        {"John","johnsmith@mail.com","john_newyork@mail.com"},
        {"Mary","mary@mail.com"}
    };
    vector<vector<string>> ans = Solution().accountsMerge(accounts);
    printResult(ans);
    return 0;
}