/*
 * @LastEditors: Yygz314 2711859393@qq.com
 * @LastEditTime: 2026-06-03 17:48:33
 */
#include<iostream>
using namespace std;

class person{
	private:
		string id;
		string name;
	public:
		person(string a,string b){
			id=a,name=b;
		} 
		virtual void show(){
			cout<<"编号："<<id<<endl;
			cout<<"姓名："<<name<<endl; 
		}
}; 

class student:public person{
	private:
		string classnum;
		double score;
	public:
		student(string c,string d,string e,double f):person(c,d){
			classnum=e,score=f;
		}
		void show(){
			person::show();
			cout<<"班号："<<classnum<<endl;
			cout<<"成绩："<<score<<endl; 
		}
};

class teacher:public person{
	private:
		string title;
		string department;
	public:
		teacher(string g,string h,string k,string l):person(g,h){
			title=k,department=l;
		}
		void show(){
			person::show();
			cout<<"职称："<<title<<endl;
			cout<<"部门："<<department<<endl;
		}
};

int main(){
	person *pc;
	student  ps("202412211402004","yc","2班",100);
	pc=&ps;
	pc->show();
	teacher pt("20242003","戴入","实验室管理员","acm团队"); 
	pc=&pt;
	pc->show();
	return 0;
}






