#include<iostream>
#include<string> 
using namespace std;

class Book{
	private:
		string title,author,publisher,isbn;
		double price;
		static int bookCount;
		static double totalPrice;
	public:
		Book(const string& t,const string a,const string& p,const string& i,double pr):title(t),author(a),publisher(p),isbn(i) ,price(pr){
			bookCount++;
			totalPrice+=price;	
		}
		void display()const{
			cout<<title<<endl;
			cout<<author<<endl;
			cout<<publisher<<endl;
			cout<<isbn<<endl;
			cout<<price<<endl<<endl;	
		}
		static int getBookCount(){
			return bookCount;
		}
		static double getTotalPrice(){
			return totalPrice;
		}
};	

int Book::bookCount=0;
double Book::totalPrice = 0;

int main(){
	Book book1("C++ Primer", "Stanley Lippman", 
              "Addison-Wesley Professional", "9780321714114", 59.99);
    Book book2("C++ 面向对象程序设计", "陈维新", 
              "中国铁道出版社有限公司", "9787113224868", 48.00);
    
    book1.display();
	book2.display();
	cout<<Book::getBookCount()<<endl<<Book::getTotalPrice()<<endl;
	return 0; 
} 
