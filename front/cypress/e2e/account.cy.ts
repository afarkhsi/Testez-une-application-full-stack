/// <reference types="Cypress" />

describe('Account Page – User Types', () => {
 const ADMIN = {
    token: 'jwt',
    type: 'Bearer',
    id: 1,
    email: 'yoga@studio.com',
    firstName: 'Admin',
    lastName: 'ADMIN',
    admin: true,
    createdAt: '2025-06-20T15:28:57',
    updatedAt: '2025-06-20T15:28:57',
    password: 'test!1234'
  };

  const USER = {
    token: 'jwt',
    type: 'Bearer',
    id: 2,
    email: 'clarkkent@gmail.com',
    firstName: 'clark',
    lastName: 'KENT',
    admin: false,
    createdAt: '2025-06-20T15:44:33',
    updatedAt: '2025-06-20T15:44:33',
    password: 'clarkkent', 
  };

  beforeEach(() => {
    cy.intercept('GET', '/api/session', (req) => {
      req.reply([]);
    });

    cy.intercept('DELETE', '/api/user');
  });

  describe('As an admin', () => {
    beforeEach(() => {
      cy.visit('/login');

      cy.intercept('POST', '/api/auth/login', ADMIN);

      cy.intercept('GET', `/api/user/${ADMIN.id}`, (req) => {
        req.reply(ADMIN);
      });

      cy.get('input[formControlName=email]').type('yoga@studio.com');
      cy.get('input[formControlName=password]').type('test!1234');
      
      cy.get('button[type=submit]').click()

      cy.url().should('include', '/sessions');
    });

    it('should show their info and NOT include a delete button', () => {
      cy.get('span.link').contains('Account').click();

      cy.url().should('include', '/me');
      
      cy.contains('h1', `User information`).should('exist');
      cy.contains('p', `Name: ${ADMIN.firstName} ${ADMIN.lastName}`).should('exist');
      cy.contains('p', `Email: ${ADMIN.email}`).should('exist');
      cy.contains('p', `You are admin`).should('exist');
      cy.contains('p', 'Create at:').should('contain', 'June 20, 2025');
      cy.contains('p', 'Last update:').should('contain', 'June 20, 2025');

      cy.get('button[mat-raised-button]').should('not.exist');
    });
  });

  describe('As a regular user', () => {
    beforeEach(() => {
      cy.visit('/login');

      cy.intercept('POST', '/api/auth/login', USER);

      cy.intercept('GET', `/api/user/${USER.id}`, (req) => {
        req.reply(USER);
      });

      cy.get('input[formControlName=email]').type('user@user.com');
      cy.get('input[formControlName=password]').type('test!1234');
      
      cy.get('button[type=submit]').click()

      cy.url().should('include', '/sessions');
    });

    it('should show their info and include a delete button', () => {
      cy.get('span.link').contains('Account').click();

      cy.url().should('include', '/me');
      
      cy.contains('h1', `User information`).should('exist');
      cy.contains('p', `Name: ${USER.firstName} ${USER.lastName}`).should('exist');
      cy.contains('p', `Email: ${USER.email}`).should('exist');
      cy.contains('p', `Delete my account`).should('exist');
      cy.contains('p', 'Create at:').should('contain', 'June 20, 2025');
      cy.contains('p', 'Last update:').should('contain', 'June 20, 2025');

      cy.get('button[mat-raised-button]').should('exist');
    });
  });
});
