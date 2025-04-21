<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .navbar {
        background-color: #ffffff;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
        padding: 15px 30px;
        position: fixed;
        top: 0;
        width: 100%;
        z-index: 1000;
    }

    .nav-wrapper {
        display: flex;
        justify-content: space-between;
        align-items: center;
        max-width: 1200px;
        margin: 0 auto;
    }

    .nav-links a,
    .nav-auth a {
        margin-right: 20px;
        text-decoration: none;
        color: #4a90e2;
        font-weight: bold;
        font-size: 24px;
        transition: color 0.2s ease;
    }

    .nav-links a:last-child,
    .nav-auth a:last-child {
        margin-right: 0;
    }

    .nav-links a:hover,
    .nav-auth a:hover {
        color: #357abd;
    }

    .nav-auth {
        display: flex;
        align-items: center;
    }

    .nav-auth span {
        margin-right: 15px;
        color: #333;
        font-size: 15px;
    }

    body {
        padding-top: 80px;
    }
</style>


<div class="navbar">
    <div class="nav-wrapper">
        <div class="nav-links">
            <a href="/home">Home</a>
            <a href="/tests">Tests</a>

            <c:if test = "${sessionScope.user.role.name() == 'TEACHER'}">
                <a href="/my-tests">My tests</a>
                <a href="/create-quiz">Create</a>
            </c:if>

            <c:if test="${sessionScope.user.role.name() == 'USER'}">
                <a href="/passed-tests">Passed tests</a>
            </c:if>

        </div>

        <div class="nav-auth">
            <c:if test="${sessionScope.user != null}">
                <span>${sessionScope.user.username}</span>
                <a href="/logout">
                    Log out
                </a>
            </c:if>

            <c:if test="${sessionScope.user == null}">
                <a href="/login">Log in</a>
                <a href="/register">Sign up</a>
            </c:if>
        </div>
    </div>
</div>

