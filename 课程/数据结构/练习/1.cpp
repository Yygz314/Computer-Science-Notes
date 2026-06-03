/*
 * @LastEditors: Yygz314 2711859393@qq.com
 * @LastEditTime: 2025-12-27 17:26:33
 */
#include<iostream>
#include<vector>
using namespace std;
#define num 6
#define course_num 5
struct Student{
    string id;
    string name;
    vector<int> score;
    int average;
}st[num];

//计算平均成绩
void average_count(){ 
    for(int i = 0;i<num;i++){
        st[i].average = 0;
        for(int j = 0;j<st[i].score.size();j++){
            st[i].average += st[i].score[j];
        }
        st[i].average /= st[i].score.size();
    }
}

//计算第一门课平均分
int firstaverage_count(){
    int sum = 0;
    for(int i =0 ;i<num;i++){
        sum += st[i].score[0]; 
    }
    return sum/num;
}

//查找有两门以上不及格学生信息
void bujige_search(){
    for(int k = 0;k<num;k++){
        int ct = 0;
        for(int i = 0;i<st[k].score.size();i++){
            if(st[k].score[i]<60){
                ct++;
            }
            if(ct>=2){
                cout<<"学号："<<st[k].id<<endl;
                cout<<"姓名："<<st[k].name<<endl;
                for(int j = 0;j<st[k].score.size();j++){
                    cout<<"成绩："<<st[k].score[j]<<" ";
                }
                cout<<endl;
                cout<<"平均成绩："<<st[k].average<<endl;
                break;
            }
        } 
    }
}

//查找90以上或全科85以上
void youxiu_search(){ 
    for(int k = 0;k<num;k++){
        if(st[k].average>90){
            cout<<"姓名："<<st[k].name<<endl;
            continue;
        }else{
            int fg = 1;
            for(int i = 0;i<st[k].score.size();i++){
                if(st[k].score[i]<85){
                    fg = 0;
                    break;
                }
            }
            if(fg){
                cout<<"姓名："<<st[k].name<<endl;
            }
        }
    }
}

int main(){
    for(int i = 0;i<num;i++){
        cout<<"请输入第"<<i+1<<"个学生的学号、姓名、成绩："<<endl;
        cin>>st[i].id>>st[i].name;
        for(int j = 0;j<course_num;j++){
            int score;
            cin>>score;
            st[i].score.push_back(score);
        }
    }
    average_count();
    cout<<"第一门课平均分："<<firstaverage_count()<<endl;
    cout<<"两门以上不及格学生信息："<<endl;
    bujige_search();
    cout<<endl;
    cout<<"90以上或全科85以上学生信息："<<endl;
    youxiu_search();
    return 0;
}


