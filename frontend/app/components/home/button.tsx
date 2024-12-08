import React from 'react';
import styles from './button.module.css';

enum ButtonColor {
  red = 'red',
  green = 'green',
  purple = 'purple'
}
interface ButtonProps {
  onClick: (e?: React.MouseEvent<HTMLButtonElement>) => void;
  text: string;
  logo?: string;
  children?: React.ReactNode;
  color?: ButtonColor;
}

const Button: React.FC<ButtonProps> = ({
  onClick,
  text,
  logo,
  children,
  color
}) => {
  const buttonClass = [styles.button];
  if (color && styles[color]) {
    buttonClass.push(styles[color]);
  }
  return (
    <button onClick={onClick} className={buttonClass.join(' ')}>
      {logo && <img src={logo} alt="logo" className={styles.logo} />}{' '}
      {/* Render logo if provided */}
      {text}
      {children}
    </button>
  );
};

export { Button, ButtonColor };
