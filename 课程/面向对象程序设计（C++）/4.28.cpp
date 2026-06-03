#include<iostream>
using namespace std;

class shang_stock;

class shen_stock{
	private:
		int general;
		int st;
		int pt;
	public:
		shen_stock(int g,int  s,int p):general(g),st(s),pt(p){}
		friend void calculate_shen_total(const shen_stock& s);
		friend void count(const shen_stock& s,const shang_stock& sh);
};

class shang_stock{
	private:
		int general;
	 	int st;
	 	int pt;
	public:
		shang_stock(int g,int s,int p):general(g),st(s),pt(p){}
		friend void calculate_shang_total(const shang_stock& sh);
		friend void count(const shen_stock& s,const shang_stock& sh);
}; 

void calculate_shen_total(const shen_stock& s){
	cout<<"深圳"<<(s.general+s.st+s.pt)<<endl; 
}

void calculate_shang_total(const shang_stock& sh){
	cout<<"上海"<<(sh.general+sh.st+sh.pt)<<endl; 
}

void count(const shen_stock& s,const shang_stock& sh){
	int total=(s.general+s.st+s.pt)+(sh.general+sh.st+sh.pt);
	cout<<"总股数"<<total<<endl; 
}

int main(){
	shen_stock shenzhen(1500,43,12);
	shang_stock shanghai(2450,27,8);
	
	calculate_shen_total(shenzhen);
	calculate_shang_total(shanghai);
	count(shenzhen,shanghai);
	
	return 0;
}
