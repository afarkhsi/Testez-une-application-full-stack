/// <reference types="Cypress" />

describe('Sessions Page', () => {
  const ADMIN = {
    token: 'jwt',
    type: 'Bearer',
    id: 1,
    username: 'yoga@studio.com',
    firstName: 'Admin',
    lastName: 'Admin',
    admin: true,
  };

  const USER = {
    token: 'jwt',
    type: 'Bearer',
    id: 2,
    username: 'clarkkent@gmail.com',
    firstName: 'clark',
    lastName: 'kent',
    admin: false,
  };

//   const SESSION = {
//     id: 2,
//     name: 'Session 1',
//     date: '2025-06-21T02:00:00.000+00:00',
//     teacher_id: 1,
//     description: 'Session de yoga',
//     users: [2],
//     createdAt: '2025-06-20T15:45:51',
//     updatedAt: '2025-06-20T15:45:51',
//   };

    const SESSION = {
    id: 1,
    name: 'Session test',
    date: '2025-06-22T02:00:00.000+00:00',
    teacher_id: 1,
    description: 'Session de yoga appronfondie',
    users: [],
    createdAt: '2025-06-20T19:45:51',
    updatedAt: '2025-06-20T19:45:51',
  };

    const SESSIONS_LIST = [SESSION];

  const UPDATED_SESSION = {
    ...SESSION,
    name: 'Session 2 updated',
  };

  const TEACHERS = [
    { id: 1, lastName: 'DELAHAYE', firstName: 'Margot' },
    { id: 2, lastName: 'THIERCELIN', firstName: 'Hélène' },
  ];

  beforeEach(() => {
    cy.intercept('GET', '/api/session', (req) => {
      req.reply(SESSIONS_LIST);
    });

    cy.intercept('POST', '/api/session', (req) => {
      SESSIONS_LIST.push(SESSION);
      req.reply(SESSION);
    });

    cy.intercept('GET', `/api/session/${SESSION.id}`, SESSION);
    cy.intercept('GET', '/api/teacher', TEACHERS);
    cy.intercept('GET', `/api/teacher/${TEACHERS[0].id}`, TEACHERS[0]);

    cy.intercept(
      'POST',
      `/api/session/${SESSION.id}/participate/${SESSION.id}`,
      (req) => {
        req.reply({
          statusCode: 200,
          body: {},
        });
      }
    );
    cy.intercept('PUT', `/api/session/${SESSION.id}`, UPDATED_SESSION);
    cy.intercept('DELETE', `/api/session/${SESSION.id}`, UPDATED_SESSION);
    cy.intercept('POST', `/api/session/${SESSION.id}/participate/${USER.id}`, (req) => {
     const updatedSession = { ...SESSION, users: [USER.id] };
     req.reply(updatedSession);
    });

    cy.intercept('DELETE', `/api/session/${SESSION.id}/participate/${USER.id}`, {});
  });

  describe('Admin', () => {
    beforeEach(() => {
      cy.visit('/login');
      cy.intercept('POST', '/api/auth/login', ADMIN);

      cy.get('input[formControlName=email]').type(ADMIN.username);
      cy.get('input[formControlName=password]').type('test!1234');
      
      cy.get('button[type=submit]').click()
      cy.url().should('include', '/sessions');
    });

    it('should list, create, edit and delete sessions', () => {
      // See existing session
      cy.get('mat-card').each(($el, index) => {
        cy.wrap($el).invoke('text').then(text => {
        console.log(`mat-card[${index}]:`, text);
        });
    });
      cy.get('mat-card').should('have.length', 2);
      cy.get('mat-card-title').should('contain', SESSION.name);

      // Create session
      cy.get('button[mat-raised-button] span').contains('Create').click();
      cy.get('input[formControlName="name"]').type(SESSION.name);
      cy.get('input[formControlName="date"]').type(SESSION.date.split('T')[0]);
      cy.get('mat-select[formControlName="teacher_id"]').click();
      cy.contains(TEACHERS[0].firstName).click();
      cy.get('textarea[formControlName="description"]').type(SESSION.description);
    
      cy.contains('Save').click();

      cy.contains('Session created !').should('exist');
      cy.get('snack-bar-container button span').contains('Close').click();

      cy.get('mat-card-title').should('contain', SESSION.name);
      cy.get('mat-card').should('have.length', 3);

      // Edit session
      cy.contains('Edit').click();
      cy.get('input[formControlName="name"]').clear().type(UPDATED_SESSION.name);
      cy.intercept('GET', '/api/session', [UPDATED_SESSION]).as('getSessions');
      cy.contains('Save').click();

      cy.contains('Session updated !').should('exist');
      cy.get('snack-bar-container button span').contains('Close').click();
      cy.wait('@getSessions');
      cy.get('mat-card-title').should('contain', UPDATED_SESSION.name);

      // Delete session
      cy.contains('Detail').click();
      cy.contains('Delete').click();

      cy.contains('Session deleted !').should('exist');
      cy.contains('Close').click();
      cy.get('mat-card').should('have.length', 2);
    });
  });

  describe('Regular user', () => {
    beforeEach(() => {
      cy.visit('/login');
      cy.intercept('POST', '/api/auth/login', USER);

      cy.get('input[formControlName=email]').type(USER.username);
      cy.get('input[formControlName=password]').type('clarkkent');
      
      cy.get('button[type=submit]').click()
      cy.url().should('include', '/sessions');
    });

    it('should be able to view and participate to a session', () => {
      // Check user does not see edit/delete
      cy.contains('Edit').should('not.exist');
      cy.contains('Detail').click();
      cy.contains('Delete').should('not.exist');

      cy.intercept('GET', `/api/session/${SESSION.id}`, { ...SESSION, users: [USER.id] }).as('updatedSession');


      // Check participation logic
      cy.get('button[mat-raised-button]').contains('Participate').click();

      cy.wait('@updatedSession');

      cy.get('button[mat-raised-button]').contains('Do not participate');

      // Check attendees count
      cy.contains(/attendees/i).invoke('text').then(text => {
        const match = text.match(/\d+/);
        const count = match && match[0] ? parseInt(match[0]) : 0;
        expect(count).to.eq(1);
      });

      cy.intercept('GET', `/api/session/${SESSION.id}`, SESSION).as('sessionAfterOptOut');

      // Opt-out
      cy.contains('Do not participate').click();
      cy.wait('@sessionAfterOptOut');
      cy.contains('Participate').should('exist');
    });
  });
});
