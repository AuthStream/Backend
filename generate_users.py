with open("users.csv", "w") as f:
    f.write("id,pass,created\n")  # Đúng tên cột

    for i in range(10000, 11000):
        f.write(f"id{i},pass{i},{i*1000}\n")  # Đủ 3 giá trị cho 3 cột
