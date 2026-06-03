#include<iostream>
#include<string>
#include<windows.h>
using namespace std;

class User{
	private:
		string username,password;
		static int userCount;
	public:
		User(const string& u,const string& p)
			:username(u),password(p){
			userCount++;
		} 
		void display() const{
			cout<<"用户名："<<username<<"\n密码："<<password<<endl<<endl; 
		}
		static int getUserCount(){
			return userCount;
		}	
}; 

int User::userCount=0;

int main(){
	int numUsers;
	cout<<"请输入用户数量：";
	cin>>numUsers;
	cin.ignore();
	User** users=new User*[numUsers];
	
	for(int i=0;i<numUsers;++i){
		string username,password;
		cout<<"第"<<i+1<<"个用户名：";
		getline(cin,username);
		
		HANDLE hStdin=GetStdHandle(STD_INPUT_HANDLE);
		DWORD mode;
		GetConsoleMode(hStdin,&mode);
		SetConsoleMode(hStdin,mode & ~ENABLE_ECHO_INPUT);
		
		cout<<"第"<<i+1<<"个密码：";
		getline(cin,password);
		
		SetConsoleMode(hStdin,mode);
		
		users[i]=new User(username,password); 
	}
	cout<<endl<<"用户信息"<<endl;
	for(int i=0;i<numUsers;++i){
		users[i]->display();
		delete users[i];
	} 
	delete[] users;
	cout<<"总用户数："<<User::getUserCount();
	
	return 0;
}
