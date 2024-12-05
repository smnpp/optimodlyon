import React from 'react';
import styles from './button.module.css';

interface ButtonProps {
  onClick: (e?: React.MouseEvent<HTMLButtonElement>) => void;
  text: string;
  logo?: string;
  children?: React.ReactNode;
}

const Button: React.FC<ButtonProps> = ({ onClick, text, logo, children }) => {
  return (
    <button
      onClick={onClick}
      className={styles.button}
    >
      {logo && <img src={logo} alt="logo" className={styles.logo} />} {/* Render logo if provided */}
      {text}
      {children}
    </button>
  );
};

export default Button;