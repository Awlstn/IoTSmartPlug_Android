from socket import socket
from multiprocessing import Process
import firebase_admin
from firebase_admin import credentials, db
import time
import random
import asyncio
import pymysql

# 사용시간(초)
timer = [0]
async def Timeto():
    while True:
        await asyncio.sleep(0.1)
        timer[0] += 1
        return timer[0]

# 선풍기
def Fan():
    sum2 = ref.child('IoT').child('1선풍기').child('sum').get()
    while True:
        sql="select * from ems_vi order by id desc limit 1"
        cur.execute(sql)
        for row in cur.fetchall():
            print(row[4])
            print(row[5])
        Time = asyncio.run(Timeto())
        #C_AC2 = round(random.random(),3)
        #E_b2 = bill(sum2)
        sum2 = sum2 + row[4]
        into('1선풍기', row[4], sum2, row[5], Time)
        if ref.child('IoT').child('1선풍기').child('C_AC').get() == 0:
            break

# 냉장고           
def Refrigerator():
    sum1 = ref.child('IoT').child('냉장고').child('sum').get()
    while True:
        Time = asyncio.run(Timeto())
        C_AC1 = round(random.random(),3)
        E_b1 = bill(sum1)
        sum1 = sum1 + C_AC1
        into('냉장고', C_AC1, sum1, E_b1, Time)
        if ref.child('IoT').child('냉장고').child('C_AC').get() == 0:
            break
    
# 에어컨       
def Air():
    sum3 = ref.child('IoT').child('에어컨').child('sum').get()
    while True:
        Time = asyncio.run(Timeto())
        C_AC3 = round(random.random(),3)
        E_b3 = bill(sum3)
        sum3 = sum3 + C_AC3
        into("에어컨",C_AC3, sum3, E_b3, Time)
        if ref.child('IoT').child('에어컨').child('C_AC').get() == 0:
            break

# 파이어베이스에 데이터 값 입력
def into(n, a, b, c, d):
    ref.child('IoT').child(n).child('C_AC').set(a)
    ref.child('IoT').child(n).child('sum').set(b)
    ref.child('IoT').child(n).child('E_bill').set(c)
    ref.child('IoT').child(n).child('Time').set(d)

# 전기세 계산(대략)        
def bill(sum):
    E_bill = 0
    if(sum<=100):
        E_bill = sum*60.7
    elif(100<sum<=200):
        E_bill = 100*60.7 + (sum-100)*125.9
    elif(200<sum<=300):
        E_bill = 100*60.7 + 100*125.9 + (sum-200)*187.9
    elif(300<sum<=400):
        E_bill = 100*60.7 + 100*125.9 + 100*187.9 + (sum-300)*280.6
    elif(400<sum<=500):
        E_bill = 100*60.7 + 100*125.9 + 100*187.9 + 100*280.6 + (sum-400)*417.7
    else:
        E_bill = 100*60.7 + 100*125.9 + 100*187.9 + 100*280.6 + 100*417.7 + (sum-500)*709.5
    return E_bill

def handle(sock): #자식 프로세스로 실행할 함수
    #문자를 수신하여 출력하고 다시 전송
    

    while True: #수신 동작은 무한 루프
        msg = sock.recv(1024)
        print(f'Received message: {msg.decode()} ')
        
        #수신데이터가 없을때
        if len(msg) == 0:
            print("수신값이 없어요")
            break
        strmsg = str(msg.decode())

        # 선풍기
        if strmsg == "1" :
            Fan()
            ref.child('IoT').child('1선풍기').child('Time').set(0)
        elif strmsg == "2":
            for i in range(10):
                ref.child('IoT').child('1선풍기').child('C_AC').set(0)
                i += 1

        # 냉장고 
        if strmsg == "3":
            Refrigerator()
            ref.child('IoT').child('냉장고').child('Time').set(0)
            ref.child('IoT').child('냉장고').child('on_off').set(0)
        elif strmsg == "4":
            for i in range(10):
                ref.child('IoT').child('냉장고').child('C_AC').set(0)
                i += 1
            
        # 에어컨
        if strmsg == "5":
            Air()
            ref.child('IoT').child('에어컨').child('Time').set(0)
        elif strmsg == "6":
            sock.send(str(6).encode("utf-8"))
            for i in range(10):
                ref.child('IoT').child('에어컨').child('C_AC').set(0)
                i += 1

        # if strmsg == "10":
        #     ref.child('IoT').child('냉장고').child('on_off').set(10)
        #     ref.child('IoT').child('냉장고').child('on_off').set(0)
        #     print(f'Received message: {msg.decode()} ')
        #     Refrigerator()
        #     ref.child('IoT').child('냉장고').child('Time').set(0)
        # elif strmsg == "20":
        #     ref.child('IoT').child('냉장고').child('on_off').set(20)
        #     print(f'Received message: {msg.decode()} ')
        #     for i in range(10):
        #         ref.child('IoT').child('냉장고').child('C_AC').set(0)
        #         i += 1
        #     ref.child('IoT').child('냉장고').child('on_off').set(0)
  
# 파이어 베이스 연결
db_url = ''
cred = credentials.Certificate("")
default_app = firebase_admin.initialize_app(cred, {'databaseURL':db_url})
ref = db.reference()
DB연결
conn = pymysql.connect(host="", user="", passwd="", db="")
cur = conn.cursor()
# Tcp소켓 연결
if __name__ == "__main__":
    sock = socket() #TCP 소켓
    addr = ('192.168.0.11', 8080)
    sock.bind(addr)
    sock.listen(4)
    print("기다리는 중..")
    while True:
        c_sock, r_addr = sock.accept()
        print(f'{r_addr} is connected')
        p1 = Process(target=handle, args=(c_sock, )) #자식 프로세스 생성
        p1.start() #자식 프로세스 시작
        time.sleep(5)
