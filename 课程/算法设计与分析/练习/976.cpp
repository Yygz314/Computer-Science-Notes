/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-04-29 10:29:50
 * @Author       : Yygz314
 * @Date         : 2026-04-29 10:28:52
 * @blog         : https://yygz314.github.io/
 */
#include<bits/stdc++.h>
using namespace std;
class Solution {
public:
    int largestPerimeter(vector<int>& nums) {
        sort(nums.begin(),nums.end());
        for(int i = nums.size()-3;i>=0;i--){
            if(nums[i]+nums[i+1]>nums[i+2]){
                return nums[i]+nums[i+1]+nums[i+2];
            }
        }
        return 0;
    }
};
int main(){
    vector<int> nums = {2,1,2};
    Solution s;
    cout<<s.largestPerimeter(nums);
    return 0;
}