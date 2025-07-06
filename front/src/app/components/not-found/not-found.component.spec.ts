import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

import { NotFoundComponent } from './not-found.component';
import { MeComponent } from '../me/me.component';
import { AuthGuard } from '../../guards/auth.guard';
import { expect } from '@jest/globals';

describe('NotFoundComponent - Integration with Router', () => {
  let router: Router;
  let location: Location;
  let fixture: ComponentFixture<NotFoundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotFoundComponent /*, MeComponent if needed*/],
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'me', component: MeComponent, canActivate: [AuthGuard] }, // ou mock
          { path: '404', component: NotFoundComponent },
          { path: '**', redirectTo: '404' },
        ]),
      ],
      providers: [
        {
          provide: AuthGuard,
          useValue: { canActivate: () => true }
        }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);

    router.initialNavigation();
  });

  it('should navigate to /404 for unknown paths and display NotFoundComponent', fakeAsync(() => {
    router.navigate(['/some/unknown/path']);
    tick();

    expect(location.path()).toBe('/404');

    fixture = TestBed.createComponent(NotFoundComponent);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain("Page not found !");
  }));
});
