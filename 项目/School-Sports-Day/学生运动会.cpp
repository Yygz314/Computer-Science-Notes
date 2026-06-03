//设计思路详见个人博客： https://www.cnblogs.com/Yygz314/p/18922934
#include<iostream>
#include<string>
#include<vector>
#include<map>
#include <algorithm>
using namespace std;

// 运动会项目基类（抽象类）
class SportsEvent
{
    protected:
        static int total;	// 静态成员：统计所有项目数量 
    public:
        virtual void display() const = 0;	// 纯虚函数，强制派生类实现多态接口 
        static int gets(){return total;}
};

int SportsEvent::total=0;	 // 静态成员初始化 

// 具体项目类
class Event:public SportsEvent
{
        string name;	// 项目名称
        string category;	// 项目类别
        int score;	// 项目分值
        friend class Management;	// 友元声明：允许Management访问私有成员 
    public:
    	// 构造/拷贝构造时自动增加项目计数 
        Event(string n,string cat,int sc):name(n),category(cat),score(sc){total++;}
        Event(const Event& E):name(E.name),category(E.category),score(E.score){total++;}
        ~Event(){total--;}	// 析构时减少计数
        
        void display()const override {cout<<"项目："<<name<<"  类别："<<category<<"  分值："<<score<<endl;} 
};

// 学生参与类
class Student:public SportsEvent
{
    protected:    
        string id;	// 学号
        string name;	// 姓名
        vector<string> events;	// 参与的项目名称列表
    public:
        Student(string sid,string n):id(sid),name(n){}
        Student(const Student& Students):id(Students.id),name(Students.name){}
        
        void display()const override {cout<<"学号："<<id<<"  姓名："<<name<<endl;}
        void join(string eventname) {events.push_back(eventname);} 	// 记录学生参与的项目
        friend class Management;	 // 友元声明 
};

// 运动会管理系统
class Management {
    vector<Student*> students;  // 学生对象指针集合
    vector<Event> events;       // 项目对象集合
    map<string, vector<Student*>> eventJoin;  // 项目名称-参与者映射（高效查询） 
public:
    ~Management() { 
        for (auto s : students) delete s;  // 析构时释放学生对象内存 
    }
    
    // 添加学生到系统
    void addStudent(Student* s) { 
        students.push_back(s); 
    }
    
    // 创建新项目
    void addEvent(string n, string c, int s) {
        events.emplace_back(n, c, s);      // 直接构造避免拷贝 
        eventJoin[n] = vector<Student*>();  // 初始化空参与列表
    }
    
    // 学生参加项目（双向关联）
    void joinEvent(string studentId, string eventName) {
        // Lambda表达式查找学生和项目 
        auto studentIt = find_if(students.begin(), students.end(), 
            [&](Student* s) { return s->id == studentId; });
        auto eventIt = find_if(events.begin(), events.end(), 
            [&](const Event& e) { return e.name == eventName; });
        
        if (studentIt != students.end() && eventIt != events.end()) {
            (*studentIt)->join(eventName);  // 更新学生参与列表
            eventJoin[eventName].push_back(*studentIt);  // 更新项目参与者
            cout << "学生 " << (*studentIt)->name << " 成功参加项目 " << eventName << endl;
        } else {
            cout << "学生或项目不存在！" << endl;
        }
    }
    
    // 查询学生参与情况
    void queryStudent(string id) {
        auto it = find_if(students.begin(), students.end(), 
            [&](Student* s) { return s->id == id; });
        
        if (it != students.end()) {
            (*it)->display();
            cout << "参与项目：" << endl;
            for (auto& eventName : (*it)->events) {
                auto eventIt = find_if(events.begin(), events.end(), 
                    [&](const Event& e) { return e.name == eventName; });
                if (eventIt != events.end()) 
                    eventIt->display();
                else 
                    cout << "-项目" << eventName << "信息缺失" << endl;
            }
        } else cout << "学生不存在";
    }
    
    // 查询项目参与情况
    void queryEvent(string evename) {
        if (eventJoin.find(evename) != eventJoin.end()) {
            cout << "项目" << evename << "的参与者：" << endl;
            for (auto student : eventJoin[evename]) 
                student->display();
            cout << "总计：" << eventJoin[evename].size() << "人" << endl;
        } else cout << "项目不存在！" << endl;
    }
    
    // 显示所有项目及参与统计
    void show() {
        cout << endl << "所有项目信息及参与情况：" << endl;
        for (auto& event : events) {
            cout << "项目名称: " << event.name << endl;
            cout << "参与人数: " << eventJoin[event.name].size() << endl;
            if (!eventJoin[event.name].empty()) {
                cout << "参与者: ";
                for (auto student : eventJoin[event.name]) 
                    cout << student->name << " ";
                cout << endl;
            }
            cout << endl;
        }
    }
};
// 主程序逻辑：数据输入→关系建立→查询→统计 
int main()
{
    Management m;
    int eventCount, studentCount, joinCount;
    cout<<"请输入项目数量：";
    cin>>eventCount;
    for(int i=0;i<eventCount;i++)
    {
        string name,cate;
        int sc;
        cout<<endl<<"项目"<<i+1<<"名称："; cin>>name;
        cout<<"项目"<<i+1<<"类别："; cin>>cate;
        cout<<"项目"<<i+1<<"分值："; cin>>sc;
        m.addEvent(name,cate,sc);
    }
    
    cout<<endl<<"请输入学生数量：";
    cin>>studentCount;
    for(int i=0;i<studentCount;i++)
    {
        string id,name;
        cout<<endl<<"学生"<<i+1<<"学号："; cin>>id;
        cout<<"学生"<<i+1<<"姓名："; cin>>name;
        m.addStudent(new Student(id,name));
    }

    cout<<endl<<"请输入记录数量：";
    cin>>joinCount;
    for(int i=0;i<joinCount;i++)
    {
        string sid,ename;
        cout<<endl<<"记录"<<i+1<<"-学号：";cin>>sid;
        cout<<"记录"<<i+1<<"-项目：";cin>>ename;
        m.joinEvent(sid,ename);  
    }
    int querystu,querye;
    cout<<endl<<"请输入要查询的人数："; cin>>querystu;
    while(querystu--)
    {
        string id;
        cout<<endl<<"请输入要查询的学号：";cin>>id;m.queryStudent(id);
    }
    cout<<endl<<"请输入要查询的项目数量："; cin>>querye;
    while(querye--)
    {
        string evename;
        cout<<endl<<"请输入要查询的项目名称：";cin>>evename;m.queryEvent(evename);
    }
    cout<<endl<<"总项目数："<<SportsEvent::gets()<<endl;
    m.show();
    return 0;
}
