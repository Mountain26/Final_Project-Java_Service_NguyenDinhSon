# Hướng dẫn test API

Base URL: `http://localhost:8080`

## Trạng thái dự án so với SRS

- `FR-01` Đăng nhập hệ thống: đã có.
- `FR-02` Xoay vòng token refresh: đã có.
- `FR-03` Đăng xuất và revoke token: đã có.
- `FR-04` Đăng ký tài khoản sinh viên: đã có.
- `FR-05` Quản lý người dùng và lớp học: đã có CRUD, tìm kiếm, phân trang.
- `FR-06` Đăng ký tham gia khóa học: đã có.
- `FR-07` Nộp bài tập / đồ án: đã có upload file qua Cloudinary, không làm link GitHub vì thầy chỉ yêu cầu file.
- `FR-08` Chấm điểm và feedback: đã có.
- `FR-09` Tải lên tài liệu bài giảng: đã có.
- `FR-10` Đổi mật khẩu / quên mật khẩu: đã có.
- `FR-11` Ghi log thời gian thực hiện: đã có.
- `FR-12` Unit test: đã có hơn 10 test.

## Lưu ý trước khi test

- Route auth dùng được cả `/api/v1/auth/**` và `/api/auth/**`.
- Theo SRS thì nên test bằng `/api/v1/auth/**`.
- Các API có bảo vệ cần header:
`Authorization: Bearer <access_token>`
- Upload file dùng `multipart/form-data`.

## Day 1

### FR-04 - Đăng ký tài khoản sinh viên

- Method: `POST`
- Path: `/api/v1/auth/register`
- Quyền: Public
- Body:
```json
{
  "username": "student1",
  "password": "123456",
  "email": "student1@gmail.com"
}
```

### FR-05 - Tạo người dùng

- Method: `POST`
- Path: `/api/v1/admin/users`
- Quyền: `ADMIN`
- Body:
```json
{
  "username": "lecturer1",
  "password": "123456",
  "email": "lecturer1@gmail.com",
  "role": "LECTURER"
}
```

### FR-05 - Danh sách người dùng

- Method: `GET`
- Path: `/api/v1/admin/users`
- Quyền: `ADMIN`
- Query:
- `keyword` tùy chọn
- `page` mặc định `0`
- `size` mặc định `10`
- Link test:
```http
GET http://localhost:8080/api/v1/admin/users?keyword=nam&page=0&size=10
Authorization: Bearer <access_token>
```

### FR-05 - Cập nhật người dùng

- Method: `PUT`
- Path: `/api/v1/admin/users/{id}`
- Quyền: `ADMIN`
- Body:
```json
{
  "email": "newmail@gmail.com",
  "active": true
}
```

### FR-05 - Xóa người dùng

- Method: `DELETE`
- Path: `/api/v1/admin/users/{id}`
- Quyền: `ADMIN`

### FR-05 - Tạo khóa học

- Method: `POST`
- Path: `/api/v1/admin/courses`
- Quyền: `ADMIN`
- Body:
```json
{
  "courseCode": "JAVA101",
  "courseName": "Java Core",
  "credit": 3
}
```

### FR-05 - Danh sách khóa học

- Method: `GET`
- Path: `/api/v1/admin/courses`
- Quyền: `ADMIN`
- Query:
- `keyword` tùy chọn
- `page` mặc định `0`
- `size` mặc định `10`
- Link test:
```http
GET http://localhost:8080/api/v1/admin/courses?keyword=JAVA&page=0&size=10
Authorization: Bearer <access_token>
```

### FR-05 - Cập nhật khóa học

- Method: `PUT`
- Path: `/api/v1/admin/courses/{id}`
- Quyền: `ADMIN`
- Body:
```json
{
  "courseCode": "JAVA102",
  "courseName": "Advanced Java",
  "credit": 4
}
```

### FR-05 - Xóa khóa học

- Method: `DELETE`
- Path: `/api/v1/admin/courses/{id}`
- Quyền: `ADMIN`

### FR-06 - Đăng ký khóa học

- Method: `POST`
- Path: `/api/v1/student/courses/enroll`
- Quyền: `STUDENT`
- Body:
```json
{
  "courseId": 1
}
```

### FR-06 - Xem các khóa học đã đăng ký

- Method: `GET`
- Path: `/api/v1/student/courses/me`
- Quyền: `STUDENT`

## Day 2

### FR-01 - Đăng nhập

- Method: `POST`
- Path: `/api/v1/auth/login`
- Quyền: Public
- Body:
```json
{
  "username": "student1",
  "password": "123456"
}
```

### FR-02 - Refresh token

- Method: `POST`
- Path: `/api/v1/auth/refresh`
- Quyền: Public
- Body:
```json
{
  "refreshToken": "your_refresh_token"
}
```
- Ghi chú: `refreshToken` là chuỗi UUID, chỉ dùng để xin `accessToken` mới. Không dùng `refreshToken` để truy cập các API `/api/v1/student/**`, `/api/v1/lecturer/**`, `/api/v1/admin/**`.

### FR-03 - Đăng xuất

- Method: `POST`
- Path: `/api/v1/auth/logout`
- Quyền: Có token đăng nhập
- Header:
`Authorization: Bearer your_access_token`
- Body tùy chọn:
```json
{
  "refreshToken": "your_refresh_token"
}
```

### FR-07 - Nộp bài theo file bằng submissionId

- Method: `POST`
- Path: `/api/v1/student/submissions/{submissionId}/upload`
- Quyền: `STUDENT`
- Content-Type: `multipart/form-data`
- Form-data:
  - `file`: file bài nộp, bắt buộc
- Ghi chú: endpoint này chỉ dùng khi `submission` đã được tạo sẵn. Flow chính của dự án hiện tại là nộp theo `courseId`.
- Link test:
```http
POST http://localhost:8080/api/v1/student/submissions/1/upload
Authorization: Bearer <access_token>
Content-Type: multipart/form-data

file=<binary file>
```

### FR-07 - Nộp bài theo file bằng courseId

- Method: `POST`
- Path: `/api/v1/student/courses/{courseId}/submissions/upload`
- Quyền: `STUDENT`
- Content-Type: `multipart/form-data`
- Form-data:
  - `file`: file bài nộp, bắt buộc
- Link test:
```http
POST http://localhost:8080/api/v1/student/courses/1/submissions/upload
Authorization: Bearer <access_token>
Content-Type: multipart/form-data

file=<binary file>
```

### FR-07 - Xem bài nộp của tôi

- Method: `GET`
- Path: `/api/v1/student/submissions/me`
- Quyền: `STUDENT`

### FR-08 - Chấm điểm và feedback

- Method: `POST`
- Path: `/api/v1/lecturer/grades`
- Quyền: `LECTURER`
- Body:
  - `submissionId`: ID của bài nộp cần chấm
  - `score`: điểm số từ `0` đến `100`
  - `feedback`: nhận xét của giảng viên
- Link test:
```http
POST http://localhost:8080/api/v1/lecturer/grades
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "submissionId": 1,
  "score": 95,
  "feedback": "Good work"
}
```

### FR-08 - Danh sách bài nộp theo khóa học

- Method: `GET`
- Path: `/api/v1/lecturer/submissions/course/{courseId}`
- Quyền: `LECTURER`

### FR-09 - Tải lên tài liệu bài giảng

- Method: `POST`
- Path: `/api/v1/lecturer/materials/upload`
- Quyền: `LECTURER`
- Content-Type: `multipart/form-data`
- Query params:
  - `courseId`: ID của khóa học cần gắn tài liệu vào, bắt buộc
  - `materialName`: tên hiển thị của tài liệu, bắt buộc
- Form-data:
  - `file`: file tài liệu cần upload, bắt buộc
- Link test:
```http
POST http://localhost:8080/api/v1/lecturer/materials/upload?courseId=1&materialName=Chapter%201
Authorization: Bearer <access_token>
Content-Type: multipart/form-data

file=<binary file>
```

### FR-09 - Danh sách tài liệu theo khóa học

- Method: `GET`
- Path: `/api/v1/lecturer/materials/course/{courseId}`
- Quyền: `LECTURER`
- Link test:
```http
GET http://localhost:8080/api/v1/lecturer/materials/course/1
Authorization: Bearer <access_token>
```

### FR-09 - Xóa tài liệu

- Method: `DELETE`
- Path: `/api/v1/lecturer/materials/{id}`
- Quyền: `LECTURER`

### FR-10 - Đổi mật khẩu

- Method: `POST`
- Path: `/api/v1/auth/change-password`
- Quyền: Có token đăng nhập
- Body:
```json
{
  "oldPassword": "123456",
  "newPassword": "newpass123"
}
```

### FR-10 - Quên mật khẩu

- Method: `POST`
- Path: `/api/v1/auth/forgot-password`
- Quyền: Public
- Body:
```json
{
  "email": "student1@gmail.com"
}
```
- Ghi chú: nếu email không tồn tại trong hê thống thì trả lỗi `Khong tim thay gmail da dang ky tai khoan`.
- OTP:
  - gửi về email đã đăng ký
  - hết hạn sau 5 phút
  - chỉ dùng 1 lần
  - tối đa 3 lần nhập sai

### FR-10 - Đặt lại mật khẩu bằng OTP

- Method: `POST`
- Path: `/api/v1/auth/reset-password`
- Quyền: Public
- Body:
```json
{
  "email": "student1@gmail.com",
  "otp": "123456",
  "newPassword": "newpass123"
}
```

## Giải thích ngắn về pagination

- `Pagination trong memory` nghĩa là hệ thống lấy toàn bộ danh sách từ database trước, sau đó mới lọc theo `keyword` và cắt trang trong RAM.
- `Query-level pagination` nghĩa là database tự trả về đúng trang cần xem ngay từ query, thường tối ưu hơn khi dữ liệu lớn.
- Hiện tại dự án đang dùng cách lọc rồi phân trang trong service. Với dữ liệu nhỏ thì ổn, nhưng nếu bảng lớn thì nên chuyển sang query-level pagination sau.

## Phần vẫn chưa khít 100% với SRS

- `FR-07` theo code hiện tại chỉ hỗ trợ upload file, không có luồng GitHub link vì bạn đã xác nhận không cần phần đó.
- `FR-05` đã có phân trang và tìm kiếm, nhưng đang làm ở tầng service sau khi lấy danh sách ra.

