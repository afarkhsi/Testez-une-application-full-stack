/// <reference types="Cypress" />

describe('Register Page', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('should display all the form elements', () => {
    cy.contains('First name');
    cy.contains('Last name');
    cy.contains('Email');
    cy.contains('Password');
    cy.contains('Submit');
  });

  it('should fill out and submit the form successfully', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: {
        id: 3,
        email: 'abdourrahmanfakhsi@mail.com',
        firstName: 'Abdourrahman',
        lastName: 'Farkhsi',
        admin: false,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
    }).as('registerRequest');

    cy.get('input[formControlName="firstName"]').type('Abdourrahman');
    cy.get('input[formControlName="lastName"]').type('Farkhsi');
    cy.get('input[formControlName="email"]').type('abdourrahmanfakhsi@mail.com');
    cy.get('input[formControlName="password"]').type('test!1234');

    cy.get('button[type="submit"]').should('not.be.disabled').click();

    cy.wait('@registerRequest');
  });

  it('should keep the submit button disabled with invalid email', () => {
    cy.get('input[formControlName="firstName"]').type('Abdourrahman');
    cy.get('input[formControlName="lastName"]').type('Farkhsi');
    cy.get('input[formControlName="email"]').type('abdourrahmanfakhsi');
    cy.get('input[formControlName="password"]').type('test!1234');

    cy.get('button[type="submit"]').should('be.disabled');
  });
});
