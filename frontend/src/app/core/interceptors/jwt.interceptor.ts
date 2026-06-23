import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {
  console.log('JwtInterceptor called for URL:', request.url);
  
  const token = localStorage.getItem('token');
  console.log('Token from localStorage:', token);

  if (token) {
    console.log('Adding Authorization header');
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(request);
};